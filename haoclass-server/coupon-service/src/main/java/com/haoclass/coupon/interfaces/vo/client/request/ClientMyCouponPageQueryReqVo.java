package com.haoclass.coupon.interfaces.vo.client.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * C端我的优惠券分页查询请求对象
 */
@Data
public class ClientMyCouponPageQueryReqVo {

    /**
     * 当前页码
     */
    @Min(value = 1, message = "当前页码必须大于0")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页数量不能超过100")
    private Long size = 10L;

    /**
     * 用户优惠券状态：0未使用 1已锁定 2已使用 3已过期
     */
    @Min(value = 0, message = "优惠券状态不正确")
    @Max(value = 3, message = "优惠券状态不正确")
    private Integer status;
}
