package com.haoclass.pay.api.dto.response;

import lombok.Data;

/**
 * 创建退款单响应对象。
 */
@Data
public class CreateRefundResponse {

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 原支付单号。
     */
    private String paymentNo;

    /**
     * 退款金额，单位：分。
     */
    private Integer refundAmount;

    /**
     * 退款状态。
     */
    private Integer status;
}
