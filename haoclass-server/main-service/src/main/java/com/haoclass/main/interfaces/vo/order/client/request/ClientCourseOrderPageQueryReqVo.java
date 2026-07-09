package com.haoclass.main.interfaces.vo.order.client.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCourseOrderPageQueryReqVo {

    /**
     * 当前页码，默认从1开始
     */
    @Min(value = 1, message = "当前页不能小于1")
    private Long current = 1L;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Long size = 10L;

    /**
     * 订单状态
     */
    @Min(value = 0, message = "订单状态不正确")
    @Max(value = 4, message = "订单状态不正确")
    private Integer status;
}