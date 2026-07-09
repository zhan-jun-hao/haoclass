package com.haoclass.coupon.domain.model.query;

import lombok.Data;

/**
 * 用户优惠券分页查询对象
 */
@Data
public class UserCouponQuery {

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页数量
     */
    private Long size;

    /**
     * 用户优惠券状态：0未使用 1已锁定 2已使用 3已过期
     */
    private Integer status;
}
