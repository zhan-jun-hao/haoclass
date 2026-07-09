package com.haoclass.coupon.interfaces.vo.admin.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCouponTemplateUpdateReqVo {

    /**
     * 优惠券名称
     */
    @NotBlank
    @Size(max = 100)
    private String couponName;

    /**
     * 优惠券描述
     */
    @Size(max = 500)
    private String description;

    /**
     * 使用门槛金额，单位：分，0表示无门槛
     */
    @Min(0)
    @NotNull
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分
     */
    @Min(1)
    @NotNull
    private Integer discountAmount;

    /**
     * 发行总数量
     */
    @Min(1)
    @NotNull
    private Integer totalStock;

    /**
     * 领取开始时间
     */
    @NotNull
    private LocalDateTime receiveStartTime;

    /**
     * 领取结束时间
     */
    @NotNull
    private LocalDateTime receiveEndTime;

    /**
     * 使用有效期开始时间
     */
    @NotNull
    private LocalDateTime validStartTime;

    /**
     * 使用有效期结束时间
     */
    @NotNull
    private LocalDateTime validEndTime;
}
