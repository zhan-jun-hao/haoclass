package com.haoclass.coupon.application.service.inner.impl;

import com.haoclass.common.exception.BusinessException;
import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.coupon.application.converter.inner.InnerUserCouponConverter;
import com.haoclass.coupon.application.service.inner.InnerUserCouponApplicationService;
import com.haoclass.coupon.domain.service.UserCouponService;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import com.haoclass.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InnerUserCouponApplicationServiceImpl implements InnerUserCouponApplicationService {

    private final UserCouponService userCouponService;

    @Override
    public List<AvailableCouponResponse> getAvailableCouponList(Integer orderAmount) {
        Long userId = UserContextHolder.getRequiredUserId();
        List<UserCoupon> coupons = userCouponService.findAvailableCoupons(userId, orderAmount);
        return InnerUserCouponConverter.INSTANCE.poToResponse(coupons);
    }

    @Override
    public AvailableCouponResponse lockCoupon(ChosenCouponReqVo reqVo) {
        Long userId = UserContextHolder.getRequiredUserId();
        UserCoupon userCoupon = userCouponService.lockCoupon(userId, reqVo.getUserCouponId(), reqVo.getOrderId(),
                reqVo.getOrderAmount(), reqVo.getLockExpireTime());
        return InnerUserCouponConverter.INSTANCE.poToResponse(userCoupon);
    }

    @Override
    public void unlockCoupon(ChosenCouponReqVo reqVo) {
        Long userId = UserContextHolder.getRequiredUserId();
        userCouponService.unlockCoupon(userId, reqVo.getUserCouponId(), reqVo.getOrderId());
    }


    @Override
    public void returnCoupons(Set<Long> ids) {
        userCouponService.returnCoupons(ids);
    }

    @Override
    public void useCoupon(ChosenCouponReqVo reqVo) {
        // 先尝试将优惠券由已锁定更新为已使用
        boolean updated = userCouponService.tryUseCoupon(
                reqVo.getUserId(),
                reqVo.getUserCouponId(),
                reqVo.getOrderId()
        );
        if(updated) {
            return;
        }
        // 更新失败可能是重复消息，检查是否已经被同一订单核销
        UserCoupon usedCoupon = userCouponService.findUsedCouponById(
                reqVo.getUserId(),
                reqVo.getUserCouponId(),
                reqVo.getOrderId()
        );
        if (usedCoupon != null) {
            return;
        }
        throw BusinessException.badRequest("优惠券未被该订单锁定或锁定已过期，无法核销");
    }

    @Override
    public void releaseExpiredLockedCoupons(LocalDateTime now) {
        userCouponService.releaseExpiredLockedCoupons(now);
    }

    @Override
    public AvailableCouponResponse getAvailableCoupon(Long id) {
        UserCoupon userCoupon = userCouponService.getById(id);
        if(Objects.isNull(userCoupon)) {
            throw BusinessException.notFound("不存在该优惠券");
        }
        return InnerUserCouponConverter.INSTANCE.poToResponse(userCoupon);
    }
}
