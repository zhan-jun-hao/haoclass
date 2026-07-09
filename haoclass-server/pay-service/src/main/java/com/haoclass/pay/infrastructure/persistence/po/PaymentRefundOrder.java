package com.haoclass.pay.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付退款订单。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_refund_order")
public class PaymentRefundOrder extends BaseEntity {

    /**
     * 退款单号，由支付服务生成并提交给第三方支付平台。
     */
    @TableField("refundNo")
    private String refundNo;

    /**
     * 原支付单号。
     */
    @TableField("paymentNo")
    private String paymentNo;

    /**
     * 业务类型。
     */
    @TableField("bizType")
    private PaymentBizTypeEnum bizType;

    /**
     * 业务订单ID，例如课程订单ID。
     */
    @TableField("bizOrderId")
    private Long bizOrderId;

    /**
     * 业务订单号快照。
     */
    @TableField("bizOrderNo")
    private String bizOrderNo;

    /**
     * 退款用户ID。
     */
    @TableField("userId")
    private Long userId;

    /**
     * 原支付金额，单位：分。
     */
    @TableField("payAmount")
    private Integer payAmount;

    /**
     * 退款金额，单位：分。
     */
    @TableField("refundAmount")
    private Integer refundAmount;

    /**
     * 货币类型，例如CNY。
     */
    @TableField("currency")
    private String currency;

    /**
     * 支付渠道。
     */
    @TableField("payChannel")
    private PaymentChannelEnum payChannel;

    /**
     * 退款状态。
     */
    @TableField("status")
    private PaymentRefundStatusEnum status;

    /**
     * 原第三方支付流水号。
     */
    @TableField("thirdTradeNo")
    private String thirdTradeNo;

    /**
     * 第三方退款流水号。
     */
    @TableField("thirdRefundNo")
    private String thirdRefundNo;

    /**
     * 退款原因。
     */
    @TableField("refundReason")
    private String refundReason;

    /**
     * 退款失败原因。
     */
    @TableField("failureReason")
    private String failureReason;

    /**
     * 退款申请时间。
     */
    @TableField("applyTime")
    private LocalDateTime applyTime;

    /**
     * 退款成功时间。
     */
    @TableField("refundTime")
    private LocalDateTime refundTime;

    /**
     * 退款关闭时间。
     */
    @TableField("closeTime")
    private LocalDateTime closeTime;

}
