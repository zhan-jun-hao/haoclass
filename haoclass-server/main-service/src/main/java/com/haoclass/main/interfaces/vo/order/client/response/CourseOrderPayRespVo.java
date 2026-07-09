package com.haoclass.main.interfaces.vo.order.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOrderPayRespVo {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 课程订单状态
     */
    private CourseOrderStatusEnum status;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 支付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 支付渠道
     */
    private Integer payChannel;

    /**
     * 支付单状态
     */
    private Integer paymentStatus;

    /**
     * 支付单过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
}
