package com.haoclass.coupon.interfaces.vo.client.response;

import com.haoclass.coupon.infrastructure.common.enums.CouponTemplateStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * c端用户优惠券基础响应对象
 */
@Data
public class ClientCouponTemplateBasicVo {

    /**
     * 优惠券模板ID
     */
    private Long id;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 使用门槛金额（单位：分），0表示无门槛
     */
    private Integer thresholdAmount;

    /**
     * 优惠金额（单位：分）
     */
    private Integer discountAmount;

    /**
     * 发放总数量
     */
    private Integer totalStock;

    /**
     * 领取开始时间
     */
    private LocalDateTime receiveStartTime;

    /**
     * 领取结束时间
     */
    private LocalDateTime receiveEndTime;

    /**
     * 使用有效期结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 优惠券模板状态
     */
    private CouponTemplateStatusEnum status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否已经领取过
     */
    private boolean isReceived;
}
