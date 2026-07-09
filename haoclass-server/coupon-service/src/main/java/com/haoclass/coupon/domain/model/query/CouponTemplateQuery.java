package com.haoclass.coupon.domain.model.query;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 优惠券模板分页查询对象
 */
@Data
public class CouponTemplateQuery {

    /**
     * 当前页数
     */
    private Long current;

    /**
     * 最大数量
     */
    private Long size;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券状态
     */
    private Integer status;

    /**
     * 开始创建时间
     */
    private LocalDateTime createTimeStart;

    /**
     * 结束创建时间
     */
    private LocalDateTime createTimeEnd;
}
