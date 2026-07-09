package com.haoclass.pay.interfaces.vo.refund.admin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端退款订单基础响应对象。
 */
@Data
public class AdminPaymentRefundOrderBasicVo {

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
     * 支付渠道。
     */
    private PaymentChannelEnum payChannel;

    /**
     * 退款状态。
     */
    private PaymentRefundStatusEnum status;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

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
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
