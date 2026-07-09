package com.haoclass.pay.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 退款成功通知请求对象
 */
@Data
public class PaymentRefundSuccessRequest {

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
     * 原支付单号。
     */
    private String paymentNo;

    /**
     * 退款单号。
     */
    private String refundNo;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

    /**
     * 退款金额，单位：分。
     */
    private Integer refundAmount;

    /**
     * 支付渠道。
     */
    private Integer payChannel;

    /**
     * 退款成功时间。
     */
    private LocalDateTime refundTime;
}
