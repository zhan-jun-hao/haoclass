package com.haoclass.pay.interfaces.vo.refund.admin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端退款订单详情响应对象。
 */
@Data
public class AdminPaymentRefundOrderDetailVo {

    /**
     * 退款订单ID。
     */
    private Long id;

    /**
     * 退款单号。
     */
    private String refundNo;

    /**
     * 原支付单号。
     */
    private String paymentNo;

    /**
     * 业务类型。
     */
    private PaymentBizTypeEnum bizType;

    /**
     * 业务订单ID。
     */
    private Long bizOrderId;

    /**
     * 业务订单号。
     */
    private String bizOrderNo;

    /**
     * 退款用户ID。
     */
    private Long userId;

    /**
     * 原支付金额，单位：分。
     */
    private Integer payAmount;

    /**
     * 退款金额，单位：分。
     */
    private Integer refundAmount;

    /**
     * 货币类型。
     */
    private String currency;

    /**
     * 支付渠道。
     */
    private PaymentChannelEnum payChannel;

    /**
     * 退款状态。
     */
    private PaymentRefundStatusEnum status;

    /**
     * 原第三方支付流水号。
     */
    private String thirdTradeNo;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

    /**
     * 退款原因。
     */
    private String refundReason;

    /**
     * 退款失败原因。
     */
    private String failureReason;

    /**
     * 退款申请时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;

    /**
     * 退款成功时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    /**
     * 退款关闭时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
