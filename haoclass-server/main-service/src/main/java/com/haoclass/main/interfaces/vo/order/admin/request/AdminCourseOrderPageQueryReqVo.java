package com.haoclass.main.interfaces.vo.order.admin.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCourseOrderPageQueryReqVo {

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
     * 订单号
     */
    private String orderNo;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 订单状态
     */
    @Min(value = 0, message = "订单状态不正确")
    @Max(value = 4, message = "订单状态不正确")
    private Integer status;

    /**
     * 支付方式：0未支付 1微信 2支付宝 3模拟支付
     */
    @Min(value = 0, message = "支付方式不正确")
    @Max(value = 3, message = "支付方式不正确")
    private Integer payType;

    /**
     * 创建时间查询开始值
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间查询结束值
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;
}