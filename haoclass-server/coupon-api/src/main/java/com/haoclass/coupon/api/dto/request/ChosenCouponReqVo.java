package com.haoclass.coupon.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 已选择优惠券请求对象
 */
@Data
public class ChosenCouponReqVo {

    /**
     * userId
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 用户优惠券id
     */
    private Long userCouponId;

    /**
     * 订单原始金额，单位：分
     */
    private Integer orderAmount;

    /**
     * 优惠券锁定截止时间，与订单过期时间保持一致
     */
    private LocalDateTime lockExpireTime;

}
