package com.haoclass.pay.api.dto.request;

import lombok.Data;

/**
 * 创建退款单请求对象
 */
@Data
public class CreateRefundRequest {

    /**
     * 业务类型：1课程订单
     */
    private Integer bizType;

    /**
     * 业务订单ID，例如课程订单ID
     */
    private Long bizOrderId;

    /**
     * 业务订单号快照
     */
    private String bizOrderNo;

    /**
     * 原支付单号
     */
    private String paymentNo;

    /**
     * 退款用户ID
     */
    private Long userId;

    /**
     * 退款金额，单位：分
     */
    private Integer refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;
}
