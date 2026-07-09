package com.haoclass.coupon.api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单可用优惠券响应对象
 */
@Data
public class AvailableCouponResponse {

    /**
     * 用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 使用门槛金额，单位：分
     */
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分
     */
    private Integer discountAmount;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;
}
