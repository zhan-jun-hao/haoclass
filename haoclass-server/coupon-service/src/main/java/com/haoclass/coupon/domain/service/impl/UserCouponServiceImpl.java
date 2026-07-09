package com.haoclass.coupon.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.coupon.domain.model.query.UserCouponQuery;
import com.haoclass.coupon.domain.service.UserCouponService;
import com.haoclass.coupon.infrastructure.common.enums.UserCouponStatusEnum;
import com.haoclass.coupon.infrastructure.persistence.mapper.UserCouponMapper;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户优惠券领域服务实现。
 */
@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return this.list(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getUserId, userId));
    }

    @Override
    public UserCoupon findByTemplateIdAndUserId(Long couponTemplateId, Long userId) {
        return this.getOne(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getCouponTemplateId, couponTemplateId)
                .eq(UserCoupon::getUserId, userId));
    }

    @Override
    public IPage<UserCoupon> pageByUserId(Long userId, UserCouponQuery query) {
        UserCouponQuery safeQuery = query == null ? new UserCouponQuery() : query;
        if (safeQuery.getCurrent() == null || safeQuery.getCurrent() < 1) {
            safeQuery.setCurrent(1L);
        }
        if (safeQuery.getSize() == null || safeQuery.getSize() < 1) {
            safeQuery.setSize(10L);
        }
        if (safeQuery.getSize() > 100) {
            safeQuery.setSize(100L);
        }

        LambdaQueryWrapper<UserCoupon> wrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getUserId, userId)
                .eq(safeQuery.getStatus() != null, UserCoupon::getStatus, safeQuery.getStatus())
                .orderByDesc(UserCoupon::getCreateTime);

        return this.page(new Page<>(safeQuery.getCurrent(), safeQuery.getSize()), wrapper);
    }

    @Override
    public void expireUnusedCoupons(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .lt(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.EXPIRED)
                .set(UserCoupon::getUpdateTime, now)
                .set(UserCoupon::getUpdatedUser, userId));
    }

    @Override
    public void saveUserCoupon(UserCoupon userCoupon) {
        if (!this.save(userCoupon)) {
            throw new BusinessException("用户优惠券保存失败");
        }
    }

    @Override
    public List<UserCoupon> findAvailableCoupons(Long userId, Integer orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        return this.list(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getUserId, userId)
                .le(UserCoupon::getThresholdAmount, orderAmount)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .ge(UserCoupon::getValidEndTime, now)
                .le(UserCoupon::getValidStartTime, now)
                .orderByDesc(UserCoupon::getDiscountAmount)
                .orderByAsc(UserCoupon::getValidEndTime));
    }

    @Override
    public UserCoupon getChosenCoupon(Long userId, Long userCouponId, Long orderId) {
        UserCoupon userCoupon = this.getOne(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED));

        if (Objects.isNull(userCoupon)) {
            throw BusinessException.notFound("该优惠券未被当前订单锁定");
        }
        return userCoupon;
    }

    @Override
    public UserCoupon lockCoupon(Long userId, Long userCouponId, Long orderId, Integer orderAmount, LocalDateTime lockExpireTime) {
        if (userCouponId == null || orderId == null || orderAmount == null || lockExpireTime == null) {
            throw BusinessException.badRequest("锁定优惠券参数不完整");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean updated = this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .isNull(UserCoupon::getOrderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .le(UserCoupon::getThresholdAmount, orderAmount)
                .ge(UserCoupon::getValidEndTime, now)
                .le(UserCoupon::getValidStartTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .set(UserCoupon::getOrderId, orderId)
                .set(UserCoupon::getLockTime, now)
                .set(UserCoupon::getLockExpireTime, lockExpireTime)
                .set(UserCoupon::getUpdatedUser, userId)
                .set(UserCoupon::getUpdateTime, now));

        if (!updated) {
            throw BusinessException.badRequest("优惠券不存在、不满足使用条件或已被占用");
        }
        return getChosenCoupon(userId, userCouponId, orderId);
    }

    @Override
    public void unlockCoupon(Long userId, Long userCouponId, Long orderId) {
        if (userCouponId == null || orderId == null) {
            throw BusinessException.badRequest("解锁优惠券参数不完整");
        }

        LocalDateTime now = LocalDateTime.now();

        // 解锁时优惠券已经自然过期，直接转为已过期
        boolean expired = this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .lt(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.EXPIRED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUpdatedUser, userId)
                .set(UserCoupon::getUpdateTime, now));
        if (expired) {
            return;
        }

        boolean unlocked = this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .ge(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUpdatedUser, userId)
                .set(UserCoupon::getUpdateTime, now));

        if(!unlocked) {
            throw BusinessException.badRequest("优惠券未被该订单锁定，无法解锁");
        }
    }

    @Override
    public void returnCoupons(Set<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        // 优惠券自身已经过期，归还后直接转为已过期
        this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .in(UserCoupon::getId, ids)
                .in(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED, UserCouponStatusEnum.USED)
                .lt(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.EXPIRED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUseTime, null)
                .set(UserCoupon::getUpdateTime, now)
                .set(UserCoupon::getUpdatedUser, 0L));

        // 优惠券自身仍然有效，归还后恢复为未使用
        this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .in(UserCoupon::getId, ids)
                .in(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED, UserCouponStatusEnum.USED)
                .ge(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUseTime, null)
                .set(UserCoupon::getUpdateTime, now)
                .set(UserCoupon::getUpdatedUser, 0L));
    }

    @Override
    public void useCoupon(Long userId, Long userCouponId, Long orderId) {
        if (userCouponId == null || orderId == null) {
            throw BusinessException.badRequest("核销优惠券参数不完整");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean updated = this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .ge(UserCoupon::getLockExpireTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.USED)
                .set(UserCoupon::getUseTime, now)
                .set(UserCoupon::getUpdatedUser, userId)
                .set(UserCoupon::getUpdateTime, now));

        if (!updated) {
            throw BusinessException.badRequest("优惠券未被该订单锁定或锁定已过期，无法核销");
        }
    }

    @Override
    public boolean tryUseCoupon(Long userId, Long userCouponId, Long orderId) {
        LocalDateTime now = LocalDateTime.now();

        return this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .ge(UserCoupon::getLockExpireTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.USED)
                .set(UserCoupon::getUseTime, now)
                .set(UserCoupon::getUpdatedUser, userId)
                .set(UserCoupon::getUpdateTime, now));
    }

    @Override
    public void releaseExpiredLockedCoupons(LocalDateTime now) {
        // 锁定到期且优惠券自身也已过期
        this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .le(UserCoupon::getLockExpireTime, now)
                .lt(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.EXPIRED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUpdatedUser, 0L)
                .set(UserCoupon::getUpdateTime, now));

        // 锁定到期但优惠券仍然有效，可以重新使用
        this.update(Wrappers.lambdaUpdate(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.LOCKED)
                .le(UserCoupon::getLockExpireTime, now)
                .ge(UserCoupon::getValidEndTime, now)
                .set(UserCoupon::getStatus, UserCouponStatusEnum.UNUSED)
                .set(UserCoupon::getOrderId, null)
                .set(UserCoupon::getLockTime, null)
                .set(UserCoupon::getLockExpireTime, null)
                .set(UserCoupon::getUpdatedUser, 0L)
                .set(UserCoupon::getUpdateTime, now));
    }

    @Override
    public UserCoupon findUsedCouponById(Long userCouponId, Long userId, Long orderId) {
        if (userCouponId == null || orderId == null) {
            throw BusinessException.badRequest("查询优惠券参数不完整");
        }
        LambdaQueryWrapper<UserCoupon> wrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getDeleted, 0)
                .eq(UserCoupon::getId, userCouponId)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getOrderId, orderId)
                .eq(UserCoupon::getStatus, UserCouponStatusEnum.USED);

        return this.getOne(wrapper);

    }
}
