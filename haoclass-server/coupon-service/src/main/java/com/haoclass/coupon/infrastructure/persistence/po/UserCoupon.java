package com.haoclass.coupon.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.coupon.infrastructure.common.enums.UserCouponStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户优惠券持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_coupon")
public class UserCoupon extends BaseEntity {

    /**
     * 优惠券模板ID。
     */
    @TableField("couponTemplateId")
    private Long couponTemplateId;

    /**
     * 领取用户ID。
     */
    @TableField("userId")
    private Long userId;

    /**
     * 优惠券名称快照。
     */
    @TableField("couponName")
    private String couponName;

    /**
     * 使用门槛金额快照，单位：分。
     */
    @TableField("thresholdAmount")
    private Integer thresholdAmount;

    /**
     * 优惠金额快照，单位：分。
     */
    @TableField("discountAmount")
    private Integer discountAmount;

    /**
     * 用户优惠券状态。
     */
    private UserCouponStatusEnum status;

    /**
     * 锁定或使用该优惠券的订单ID。
     */
    @TableField("orderId")
    private Long orderId;

    /**
     * 优惠券锁定时间。
     */
    @TableField("lockTime")
    private LocalDateTime lockTime;

    /**
     * 锁定过期时间。
     */
    @TableField("lockExpireTime")
    private LocalDateTime lockExpireTime;

    /**
     * 实际使用时间。
     */
    @TableField("useTime")
    private LocalDateTime useTime;

    /**
     * 有效期开始时间。
     */
    @TableField("validStartTime")
    private LocalDateTime validStartTime;

    /**
     * 有效期结束时间。
     */
    @TableField("validEndTime")
    private LocalDateTime validEndTime;
}
