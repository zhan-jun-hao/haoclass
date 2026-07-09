package com.haoclass.pay.interfaces.vo.payment.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * C端支付订单详情响应对象
 */
@Data
public class ClientPaymentOrderDetailVo {

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 业务类型
     */
    private PaymentBizTypeEnum bizType;

    /**
     * 业务订单号
     */
    private String bizOrderNo;

    /**
     * 支付标题
     */
    private String subject;

    /**
     * 支付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 支付渠道
     */
    private PaymentChannelEnum payChannel;

    /**
     * 支付状态
     */
    private PaymentStatusEnum status;

    /**
     * 支付单过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 支付成功时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 支付单关闭时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closeTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
