package com.haoclass.pay.interfaces.vo.payment.admin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端支付订单基础响应对象。
 */
@Data
public class AdminPaymentOrderBasicVo {

    /**
     * 支付订单ID。
     */
    private Long id;

    /**
     * 支付单号。
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
     * 支付用户ID。
     */
    private Long userId;

    /**
     * 支付标题。
     */
    private String subject;

    /**
     * 支付金额，单位：分。
     */
    private Integer payAmount;

    /**
     * 货币类型。
     */
    private String currency;

    /**
     * 支付渠道。
     */
    private PaymentChannelEnum payChannel;

    /**
     * 支付状态。
     */
    private PaymentStatusEnum status;

    /**
     * 第三方支付流水号。
     */
    private String thirdTradeNo;

    /**
     * 支付单过期时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 支付成功时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
