package com.haoclass.coupon.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.coupon.infrastructure.common.enums.CouponTemplateStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 优惠券模板持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coupon_template")
public class CouponTemplate extends BaseEntity {

    /**
     * 优惠券名称。
     */
    @TableField("couponName")
    private String couponName;

    /**
     * 优惠券说明。
     */
    private String description;

    /**
     * 使用门槛金额，单位：分，0表示无门槛。
     */
    @TableField("thresholdAmount")
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分。
     */
    @TableField("discountAmount")
    private Integer discountAmount;

    /**
     * 发行总数量。
     */
    @TableField("totalStock")
    private Integer totalStock;

    /**
     * 已领取数量。
     */
    @TableField("receivedCount")
    private Integer receivedCount;

    /**
     * 领取开始时间。
     */
    @TableField("receiveStartTime")
    private LocalDateTime receiveStartTime;

    /**
     * 领取结束时间。
     */
    @TableField("receiveEndTime")
    private LocalDateTime receiveEndTime;

    /**
     * 使用有效期开始时间。
     */
    @TableField("validStartTime")
    private LocalDateTime validStartTime;

    /**
     * 使用有效期结束时间。
     */
    @TableField("validEndTime")
    private LocalDateTime validEndTime;

    /**
     * 优惠券模板状态。
     */
    private CouponTemplateStatusEnum status;
}
