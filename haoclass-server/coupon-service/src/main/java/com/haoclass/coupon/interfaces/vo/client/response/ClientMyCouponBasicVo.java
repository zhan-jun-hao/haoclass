package com.haoclass.coupon.interfaces.vo.client.response;

import com.haoclass.coupon.infrastructure.common.enums.UserCouponStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * C端我的优惠券基础响应对象
 */
@Data
public class ClientMyCouponBasicVo {

    /**
     * 用户优惠券ID
     */
    private Long id;

    /**
     * 优惠券模板ID
     */
    private Long couponTemplateId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 使用门槛金额，单位：分，0表示无门槛
     */
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分
     */
    private Integer discountAmount;

    /**
     * 用户优惠券状态
     */
    private UserCouponStatusEnum status;

    /**
     * 锁定或使用该优惠券的订单ID
     */
    private Long orderId;

    /**
     * 优惠券锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 锁定过期时间
     */
    private LocalDateTime lockExpireTime;

    /**
     * 实际使用时间
     */
    private LocalDateTime useTime;

    /**
     * 有效期开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 领取时间
     */
    private LocalDateTime createTime;
}
