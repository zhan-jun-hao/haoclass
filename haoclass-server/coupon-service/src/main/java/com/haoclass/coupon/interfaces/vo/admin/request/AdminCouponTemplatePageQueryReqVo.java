package com.haoclass.coupon.interfaces.vo.admin.request;

import com.haoclass.coupon.infrastructure.common.enums.CouponTemplateStatusEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 优惠券分页查询请求对象
 */
@Data
public class AdminCouponTemplatePageQueryReqVo {

    /**
     * 当前页数
     */
    @Min(value = 1)
    private Long current = 1L;

    /**
     * 页面大小
     */
    @Min(value = 1)
    @Max(value = 100)
    private Long size = 10L;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券状态
     * @see CouponTemplateStatusEnum
     */
    @Min(value = 0)
    @Max(value = 2)
    private Integer status;

    /**
     * 优惠券开始创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    /**
     * 优惠券结束创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;
}
