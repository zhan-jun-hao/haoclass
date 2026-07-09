package com.haoclass.main.interfaces.vo.order.client.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单可用优惠券响应对象
 */
@Data
public class CourseOrderAvailableCouponRespVo {

    /**
     * 用户优惠券ID，创建订单时需要提交这个ID
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
     * 使用优惠券后的预计支付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;
}
