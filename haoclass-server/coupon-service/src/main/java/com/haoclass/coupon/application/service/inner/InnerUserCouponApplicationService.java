package com.haoclass.coupon.application.service.inner;

import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface InnerUserCouponApplicationService {

    /**
     * 查询当前用户可用优惠券
     */
    List<AvailableCouponResponse> getAvailableCouponList(Integer orderAmount);

    /**
     * 订单锁定优惠券
     *
     * @param reqVo
     */
    AvailableCouponResponse lockCoupon(ChosenCouponReqVo reqVo);

    /**
     * 订单解锁优惠券
     *
     * @param reqVo
     */
    void unlockCoupon(ChosenCouponReqVo reqVo);

    /**
     * 批量解锁优惠券
     *
     * @param ids
     */
    void returnCoupons(Set<Long> ids);

    /**
     * 支付成功后核销优惠券
     *
     * @param reqVo 订单优惠券请求对象
     */
    void useCoupon(ChosenCouponReqVo reqVo);

    /**
     * 释放锁定已过期的优惠券
     *
     * @param now 当前时间
     */
    void releaseExpiredLockedCoupons(LocalDateTime now);

    /**
     * 查询可用优惠券
     *
     * @param id
     * @return
     */
    AvailableCouponResponse getAvailableCoupon(Long id);
}
