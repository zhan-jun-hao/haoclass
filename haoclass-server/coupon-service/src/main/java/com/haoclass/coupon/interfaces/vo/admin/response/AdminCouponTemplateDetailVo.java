package com.haoclass.coupon.interfaces.vo.admin.response;

import com.haoclass.coupon.infrastructure.common.enums.CouponTemplateStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 优惠券详情响应对象
 */
@Data
public class AdminCouponTemplateDetailVo {

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 使用门槛金额，单位：分，0表示无门槛
     */
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分
     */
    private Integer discountAmount;

    /**
     * 发行总数量
     */
    private Integer totalStock;

    /**
     * 已领取数量
     */
    private Integer receivedCount;

    /**
     * 领取开始时间
     */
    private LocalDateTime receiveStartTime;

    /**
     * 领取结束时间
     */
    private LocalDateTime receiveEndTime;

    /**
     * 使用有效期开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 使用有效期结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 优惠券状态
     * @see CouponTemplateStatusEnum
     */
    private CouponTemplateStatusEnum status;

    /**
     * 创建人ID
     */
    private Long createdUser;

    /**
     * 最后修改人ID
     */
    private Long updatedUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
