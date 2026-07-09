package com.haoclass.pay.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付订单成功通知请求对象
 */
@Data
public class PaymentSuccessRequest {

    /**
     * 业务类型
     */
    private Integer bizType;

    /**
     * 业务订单ID
     */
    private Long bizOrderId;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 第三方支付流水号
     */
    private String thirdTradeNo;

    /**
     * 实际支付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 支付渠道
     */
    private Integer payChannel;

    /**
     * 支付成功时间
     */
    private LocalDateTime payTime;
}
