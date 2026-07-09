package com.haoclass.coupon.application.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.result.PageResult;
import com.haoclass.coupon.application.CouponTemplateApplicationService;
import com.haoclass.coupon.application.converter.admin.AdminCouponTemplateConverter;
import com.haoclass.coupon.application.converter.client.ClientCouponTemplateConverter;
import com.haoclass.coupon.application.converter.command.CouponTemplateCommandConverter;
import com.haoclass.coupon.domain.model.query.CouponTemplateQuery;
import com.haoclass.coupon.domain.service.CouponTemplateService;
import com.haoclass.coupon.domain.service.UserCouponService;
import com.haoclass.coupon.infrastructure.common.enums.UserCouponStatusEnum;
import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplatePageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateUpdateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateBasicVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateDetailVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientCouponTemplateBasicVo;
import com.haoclass.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponTemplateApplicationServiceImpl implements CouponTemplateApplicationService {

    private final CouponTemplateService couponTemplateService;
    private final UserCouponService userCouponService;
    private final RedissonClient redissonClient;

    @Override
    public PageResult<AdminCouponTemplateBasicVo> getCouponTemplatePageList(AdminCouponTemplatePageQueryReqVo reqVo) {
        CouponTemplateQuery query = AdminCouponTemplateConverter.INSTANCE.pageReqVoToQuery(reqVo);
        IPage<CouponTemplate> page = couponTemplateService.pageList(query);
        return PageResult.success(page, AdminCouponTemplateConverter.INSTANCE.poToBasicVo(page.getRecords()));
    }

    @Override
    public AdminCouponTemplateDetailVo getCouponTemplateDetail(Long id) {
        CouponTemplate couponTemplate = couponTemplateService.getCouponTemplateById(id);
        return AdminCouponTemplateConverter.INSTANCE.poToDetailVo(couponTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCouponTemplate(AdminCouponTemplateReqVo reqVo) {
        CouponTemplate couponTemplate = CouponTemplateCommandConverter.INSTANCE.createReqVoToPo(reqVo);
        couponTemplateService.saveCouponTemplate(couponTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCouponTemplate(Long id, AdminCouponTemplateUpdateReqVo reqVo) {
        CouponTemplate couponTemplate = CouponTemplateCommandConverter.INSTANCE.updateReqVoToPo(reqVo);
        couponTemplateService.updateCouponTemplateById(id, couponTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCouponTemplate(Long id) {
        couponTemplateService.deleteCouponTemplateById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishCouponTemplate(Long id) {
        couponTemplateService.publishCouponTemplate(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopCouponTemplate(Long id) {
        couponTemplateService.stopCouponTemplate(id);
    }

    @Override
    public List<ClientCouponTemplateBasicVo> getClientCouponList() {
        // 1.获取已发布且可领取的优惠券
        List<CouponTemplate> publishedList = couponTemplateService.findPublishedList();
        if(publishedList.isEmpty()) {
            return Collections.emptyList();
        }
        // 2.查询当前用户已经领取过的优惠券模板ID
        Set<Long> receivedTemplateIds = userCouponService.findByUserId(UserContextHolder.getRequiredUserId())
                .stream()
                .map(UserCoupon::getCouponTemplateId)
                .collect(Collectors.toSet());

        // 3.转换响应对象并标记是否已经领取
        List<ClientCouponTemplateBasicVo> resultList =
                ClientCouponTemplateConverter.INSTANCE.poToBasicVo(publishedList);
        resultList.forEach(item -> item.setReceived(receivedTemplateIds.contains(item.getId())));
        return resultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCouponTemplate(Long id) {
        Long userId = UserContextHolder.getRequiredUserId();
        String lockKey = "coupon:receive:" + id + ":" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            // 不指定租约时间，让 Redisson 看门狗在业务执行期间自动续期
            locked = lock.tryLock(1, TimeUnit.SECONDS);
            if (!locked) {
                throw BusinessException.badRequest("请勿重复领取优惠券");
            }

            // 1.校验优惠券已发布、在领取时间内且还有库存
            CouponTemplate couponTemplate = couponTemplateService.findPublishedOne(id);
            if (Objects.isNull(couponTemplate)) {
                throw BusinessException.badRequest("该优惠券不存在或不可领取");
            }

            // 2.提前查询用于返回友好提示，数据库唯一索引负责最终防重
            UserCoupon userCoupon = userCouponService.findByTemplateIdAndUserId(id, userId);
            if (Objects.nonNull(userCoupon)) {
                throw BusinessException.badRequest("该用户已经领取过该优惠券");
            }

            // 3.数据库条件原子更新，防止不同用户并发领取时库存超发
            couponTemplateService.increaseReceivedCount(id);

            // 4.创建用户优惠券领取记录
            UserCoupon newUserCoupon = createUserCoupon(couponTemplate, userId);
            try {
                userCouponService.saveUserCoupon(newUserCoupon);
            } catch (DuplicateKeyException exception) {
                throw BusinessException.badRequest("该用户已经领取过该优惠券");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException("领取优惠券被中断，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private UserCoupon createUserCoupon(CouponTemplate couponTemplate, Long userId) {
        UserCoupon newUserCoupon = new UserCoupon();
        newUserCoupon.setId(IdUtil.getSnowflakeNextId());
        newUserCoupon.setCouponName(couponTemplate.getCouponName());
        newUserCoupon.setCouponTemplateId(couponTemplate.getId());
        newUserCoupon.setStatus(UserCouponStatusEnum.UNUSED);
        newUserCoupon.setUserId(userId);
        newUserCoupon.setDiscountAmount(couponTemplate.getDiscountAmount());
        newUserCoupon.setThresholdAmount(couponTemplate.getThresholdAmount());
        newUserCoupon.setValidStartTime(couponTemplate.getValidStartTime());
        newUserCoupon.setValidEndTime(couponTemplate.getValidEndTime());
        LocalDateTime now = LocalDateTime.now();
        newUserCoupon.setCreateTime(now);
        newUserCoupon.setUpdateTime(now);
        newUserCoupon.setCreatedUser(userId);
        newUserCoupon.setUpdatedUser(userId);
        newUserCoupon.setDeleted(0);
        return newUserCoupon;
    }

}
