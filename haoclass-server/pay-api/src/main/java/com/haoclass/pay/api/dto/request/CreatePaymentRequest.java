package com.haoclass.pay.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建支付订单请求对象
 */
@Data
public class CreatePaymentRequest {

    private Integer bizType;

    private Long bizOrderId;

    private String bizOrderNo;

    private Long userId;

    private String subject;

    private Integer payAmount;

    private Integer payChannel;

    private LocalDateTime expireTime;
}