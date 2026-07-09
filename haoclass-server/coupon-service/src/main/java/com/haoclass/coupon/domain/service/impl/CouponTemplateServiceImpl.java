package com.haoclass.coupon.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.coupon.domain.model.query.CouponTemplateQuery;
import com.haoclass.coupon.domain.service.CouponTemplateService;
import com.haoclass.coupon.infrastructure.common.enums.CouponTemplateStatusEnum;
import com.haoclass.coupon.infrastructure.persistence.mapper.CouponTemplateMapper;
import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.security.context.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate>
        implements CouponTemplateService {

    @Override
    public IPage<CouponTemplate> pageList(CouponTemplateQuery query) {
        CouponTemplateQuery safeQuery = query == null ? new CouponTemplateQuery() : query;
        if (safeQuery.getCurrent() == null || safeQuery.getCurrent() < 1) {
            safeQuery.setCurrent(1L);
        }
        if (safeQuery.getSize() == null || safeQuery.getSize() < 1) {
            safeQuery.setSize(10L);
        }
        if (safeQuery.getSize() > 100) {
            safeQuery.setSize(100L);
        }
        if (safeQuery.getCreateTimeStart() != null
                && safeQuery.getCreateTimeEnd() != null
                && safeQuery.getCreateTimeStart().isAfter(safeQuery.getCreateTimeEnd())) {
            throw BusinessException.badRequest("开始时间不能大于结束时间");
        }

        LambdaQueryWrapper<CouponTemplate> wrapper = Wrappers.lambdaQuery(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .like(StringUtils.hasText(safeQuery.getCouponName()),
                        CouponTemplate::getCouponName, safeQuery.getCouponName())
                .eq(safeQuery.getStatus() != null, CouponTemplate::getStatus, safeQuery.getStatus())
                .ge(safeQuery.getCreateTimeStart() != null,
                        CouponTemplate::getCreateTime, safeQuery.getCreateTimeStart())
                .le(safeQuery.getCreateTimeEnd() != null,
                        CouponTemplate::getCreateTime, safeQuery.getCreateTimeEnd())
                .orderByDesc(CouponTemplate::getCreateTime);

        return this.page(new Page<>(safeQuery.getCurrent(), safeQuery.getSize()), wrapper);
    }

    @Override
    public CouponTemplate getCouponTemplateById(Long id) {
        CouponTemplate couponTemplate = this.getById(id);
        if (couponTemplate == null) {
            throw BusinessException.notFound("该优惠券不存在");
        }
        return couponTemplate;
    }

    @Override
    public void saveCouponTemplate(CouponTemplate couponTemplate) {
        validateCouponTemplate(couponTemplate);

        Long userId = UserContextHolder.getRequiredUserId();
        LocalDateTime now = LocalDateTime.now();
        couponTemplate.setId(IdUtil.getSnowflakeNextId());
        couponTemplate.setReceivedCount(0);
        couponTemplate.setStatus(CouponTemplateStatusEnum.DRAFT);
        couponTemplate.setCreateTime(now);
        couponTemplate.setUpdateTime(now);
        couponTemplate.setCreatedUser(userId);
        couponTemplate.setUpdatedUser(userId);
        couponTemplate.setDeleted(0);

        boolean saved = this.save(couponTemplate);
        if (!saved) {
            throw new BusinessException("新增优惠券失败");
        }
    }

    @Override
    public void updateCouponTemplateById(Long id, CouponTemplate couponTemplate) {
        CouponTemplate origin = getCouponTemplateById(id);
        if (origin.getStatus() != CouponTemplateStatusEnum.DRAFT) {
            throw BusinessException.badRequest("只有草稿状态的优惠券才能修改");
        }
        validateCouponTemplate(couponTemplate);

        int receivedCount = Objects.requireNonNullElse(origin.getReceivedCount(), 0);
        if (couponTemplate.getTotalStock() < receivedCount) {
            throw BusinessException.badRequest("优惠券发行总数量不能小于已领取总数量");
        }

        boolean updated = this.update(Wrappers.lambdaUpdate(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getId, id)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.DRAFT)
                .set(CouponTemplate::getCouponName, couponTemplate.getCouponName())
                .set(CouponTemplate::getDescription, couponTemplate.getDescription())
                .set(CouponTemplate::getThresholdAmount, couponTemplate.getThresholdAmount())
                .set(CouponTemplate::getDiscountAmount, couponTemplate.getDiscountAmount())
                .set(CouponTemplate::getTotalStock, couponTemplate.getTotalStock())
                .set(CouponTemplate::getReceiveStartTime, couponTemplate.getReceiveStartTime())
                .set(CouponTemplate::getReceiveEndTime, couponTemplate.getReceiveEndTime())
                .set(CouponTemplate::getValidStartTime, couponTemplate.getValidStartTime())
                .set(CouponTemplate::getValidEndTime, couponTemplate.getValidEndTime())
                .set(CouponTemplate::getUpdateTime, LocalDateTime.now())
                .set(CouponTemplate::getUpdatedUser, UserContextHolder.getRequiredUserId()));
        if (!updated) {
            throw BusinessException.badRequest("优惠券修改失败");
        }
    }

    @Override
    public void deleteCouponTemplateById(Long id) {
        getCouponTemplateById(id);

        boolean deleted = this.update(Wrappers.lambdaUpdate(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getId, id)
                .in(CouponTemplate::getStatus, List.of(CouponTemplateStatusEnum.DRAFT, CouponTemplateStatusEnum.STOPPED))
                .set(CouponTemplate::getDeleted, 1)
                .set(CouponTemplate::getUpdateTime, LocalDateTime.now())
                .set(CouponTemplate::getUpdatedUser, UserContextHolder.getRequiredUserId()));
        if (!deleted) {
            throw BusinessException.badRequest("优惠券模板删除失败");
        }
    }

    @Override
    public void publishCouponTemplate(Long id) {
        CouponTemplate couponTemplate = getCouponTemplateById(id);
        if (couponTemplate.getStatus() != CouponTemplateStatusEnum.DRAFT) {
            throw BusinessException.badRequest("只有草稿状态的优惠券才能发布");
        }
        validateCouponTemplate(couponTemplate);
        if (!couponTemplate.getReceiveEndTime().isAfter(LocalDateTime.now())) {
            throw BusinessException.badRequest("优惠券领取结束时间必须晚于当前时间");
        }
        updateStatus(id, CouponTemplateStatusEnum.DRAFT, CouponTemplateStatusEnum.PUBLISHED);
    }

    @Override
    public void stopCouponTemplate(Long id) {
        CouponTemplate couponTemplate = getCouponTemplateById(id);
        if(couponTemplate.getStatus() != CouponTemplateStatusEnum.PUBLISHED) {
            throw BusinessException.badRequest("只有发布了的优惠券才能停用");
        }
        updateStatus(id, CouponTemplateStatusEnum.PUBLISHED, CouponTemplateStatusEnum.STOPPED);
    }

    private void updateStatus(Long id, CouponTemplateStatusEnum currentStatus, CouponTemplateStatusEnum targetStatus) {
        boolean updated = this.update(Wrappers.lambdaUpdate(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getId, id)
                .eq(CouponTemplate::getStatus, currentStatus)
                .set(CouponTemplate::getStatus, targetStatus)
                .set(CouponTemplate::getUpdateTime, LocalDateTime.now())
                .set(CouponTemplate::getUpdatedUser, UserContextHolder.getRequiredUserId()));
        if (!updated) {
            throw BusinessException.badRequest("优惠券状态已经发生变化，请刷新后重试");
        }
    }

    private void validateCouponTemplate(CouponTemplate couponTemplate) {
        if (couponTemplate == null) {
            throw BusinessException.badRequest("优惠券模板不能为空");
        }
        if (!StringUtils.hasText(couponTemplate.getCouponName())) {
            throw BusinessException.badRequest("优惠券名称不能为空");
        }
        couponTemplate.setCouponName(couponTemplate.getCouponName().trim());
        if (!StringUtils.hasText(couponTemplate.getDescription())) {
            couponTemplate.setDescription("");
        }
        if (couponTemplate.getThresholdAmount() == null || couponTemplate.getThresholdAmount() < 0) {
            throw BusinessException.badRequest("优惠券门槛金额不能小于0");
        }
        if (couponTemplate.getDiscountAmount() == null || couponTemplate.getDiscountAmount() <= 0) {
            throw BusinessException.badRequest("优惠券优惠金额必须大于0");
        }
        if (couponTemplate.getThresholdAmount() > 0
                && couponTemplate.getDiscountAmount() > couponTemplate.getThresholdAmount()) {
            throw BusinessException.badRequest("优惠券优惠金额不能大于门槛金额");
        }
        if (couponTemplate.getTotalStock() == null || couponTemplate.getTotalStock() <= 0) {
            throw BusinessException.badRequest("优惠券发行总数量必须大于0");
        }
        if (couponTemplate.getReceiveStartTime() == null || couponTemplate.getReceiveEndTime() == null
                || couponTemplate.getValidStartTime() == null || couponTemplate.getValidEndTime() == null) {
            throw BusinessException.badRequest("优惠券领取时间和有效时间不能为空");
        }
        if (!couponTemplate.getReceiveStartTime().isBefore(couponTemplate.getReceiveEndTime())) {
            throw BusinessException.badRequest("优惠券开始领取时间必须早于结束领取时间");
        }
        if (!couponTemplate.getValidStartTime().isBefore(couponTemplate.getValidEndTime())) {
            throw BusinessException.badRequest("优惠券可用开始时间必须早于可用结束时间");
        }
        if (couponTemplate.getValidEndTime().isBefore(couponTemplate.getReceiveEndTime())) {
            throw BusinessException.badRequest("优惠券可用结束时间不能早于领取结束时间");
        }
    }

    @Override
    public List<CouponTemplate> findPublishedList() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<CouponTemplate> wrapper = Wrappers.lambdaQuery(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.PUBLISHED)
                .le(CouponTemplate::getReceiveStartTime, now)
                .ge(CouponTemplate::getReceiveEndTime, now)
                .apply("receivedCount < totalStock")
                .orderByAsc(CouponTemplate::getReceiveEndTime);

        return this.list(wrapper);
    }

    @Override
    public CouponTemplate findPublishedOne(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<CouponTemplate> wrapper = Wrappers.lambdaQuery(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getId, id)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.PUBLISHED)
                .le(CouponTemplate::getReceiveStartTime, now)
                .ge(CouponTemplate::getReceiveEndTime, now)
                .apply("receivedCount < totalStock");

        return this.getOne(wrapper);
    }

    @Override
    public void increaseReceivedCount(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<CouponTemplate> wrapper = Wrappers.lambdaUpdate(CouponTemplate.class)
                .eq(CouponTemplate::getDeleted, 0)
                .eq(CouponTemplate::getId, id)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.PUBLISHED)
                .le(CouponTemplate::getReceiveStartTime, now)
                .ge(CouponTemplate::getReceiveEndTime, now)
                .apply("receivedCount < totalStock")

                .setIncrBy(CouponTemplate::getReceivedCount, 1)
                .set(CouponTemplate::getUpdateTime, now);

        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.badRequest("优惠券已领完或当前不可领取");
        }
    }
}
