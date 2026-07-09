package com.haoclass.pay.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_order")
public class PaymentOrder extends BaseEntity {

    /**
     * 支付单号，由支付服务生成并提交给第三方支付平台。
     */
    @TableField("paymentNo")
    private String paymentNo;

    /**
     * 业务类型
     */
    @TableField("bizType")
    private PaymentBizTypeEnum bizType;

    /**
     * 业务订单ID，例如课程订单ID
     */
    @TableField("bizOrderId")
    private Long bizOrderId;

    /**
     * 业务订单号快照
     */
    @TableField("bizOrderNo")
    private String bizOrderNo;

    /**
     * 支付用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 支付标题
     */
    @TableField("subject")
    private String subject;

    /**
     * 支付金额，单位：分
     */
    @TableField("payAmount")
    private Integer payAmount;

    /**
     * 货币类型，例如CNY
     */
    @TableField("currency")
    private String currency;

    /**
     * 支付渠道
     */
    @TableField("payChannel")
    private PaymentChannelEnum payChannel;

    /**
     * 支付状态
     */
    @TableField("status")
    private PaymentStatusEnum status;

    /**
     * 第三方支付流水号
     */
    @TableField("thirdTradeNo")
    private String thirdTradeNo;

    /**
     * 微信Native支付二维码内容
     */
    @TableField("codeUrl")
    private String codeUrl;

    /**
     * 支付失败原因
     */
    @TableField("failureReason")
    private String failureReason;

    /**
     * 支付单过期时间
     */
    @TableField("expireTime")
    private LocalDateTime expireTime;

    /**
     * 支付成功时间
     */
    @TableField("payTime")
    private LocalDateTime payTime;

    /**
     * 支付单关闭时间
     */
    @TableField("closeTime")
    private LocalDateTime closeTime;

}
