package com.haoclass.pay.infrastructure.adapter.dto;

import lombok.Data;

@Data
public class ChannelPaymentResultDto {

    /**
     * 微信Native支付二维码内容
     */
    private String codeUrl;
}