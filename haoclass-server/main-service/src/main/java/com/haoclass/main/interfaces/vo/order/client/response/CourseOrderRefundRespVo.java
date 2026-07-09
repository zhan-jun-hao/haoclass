package com.haoclass.main.interfaces.vo.order.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程订单退款响应对象
 */
@Data
public class CourseOrderRefundRespVo {

    /**
     * 课程订单ID
     */
    private Long id;

    /**
     * 课程订单号
     */
    private String orderNo;

    /**
     * 课程订单状态
     */
    private CourseOrderStatusEnum status;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 退款金额，单位：分
     */
    private Integer refundAmount;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 退款申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;
}
