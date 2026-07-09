package com.haoclass.pay.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付单关闭通知请求对象
 */
@Data
public class PaymentClosedRequest {

    /**
     * 事件类型，例如：payment.failed、payment.expired。
     */
    private String eventType;

    /**
     * 业务类型。
     */
    private Integer bizType;

    /**
     * 业务订单ID。
     */
    private Long bizOrderId;

    /**
     * 业务订单号。
     */
    private String bizOrderNo;

    /**
     * 支付单号。
     */
    private String paymentNo;

    /**
     * 支付金额，单位：分。
     */
    private Integer payAmount;

    /**
     * 支付渠道。
     */
    private Integer payChannel;

    /**
     * 支付单状态。
     */
    private Integer paymentStatus;

    /**
     * 关闭时间。
     */
    private LocalDateTime closeTime;

    /**
     * 关闭原因。
     */
    private String closeReason;
}
