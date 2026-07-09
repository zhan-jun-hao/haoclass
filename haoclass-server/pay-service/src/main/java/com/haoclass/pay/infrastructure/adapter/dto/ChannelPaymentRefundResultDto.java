package com.haoclass.pay.infrastructure.adapter.dto;

import lombok.Data;

@Data
public class ChannelPaymentRefundResultDto {

    /**
     * 第三方是否已受理退款
     */
    private Boolean accepted;

    /**
     * 第三方退款受理状态
     */
    private String channelStatus;
}
