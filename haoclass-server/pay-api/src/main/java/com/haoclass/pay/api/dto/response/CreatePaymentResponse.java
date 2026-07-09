package com.haoclass.pay.api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付订单响应对象
 */
@Data
public class CreatePaymentResponse {

    private String paymentNo;

    private Integer payAmount;

    private Integer payChannel;

    private Integer status;

    private LocalDateTime expireTime;

    /**
     * 微信Native支付二维码内容
     */
    private String codeUrl;
}
