package com.haoclass.pay.infrastructure.adapter;

import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentRefundResultDto;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentResultDto;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;

public interface PayChannelAdapter {

    /**
     * 当前适配器支持的支付渠道
     */
    PaymentChannelEnum getChannel();

    /**
     * 创建第三方支付
     */
    ChannelPaymentResultDto createPayment(PaymentOrder paymentOrder);

    /**
     * 创建第三方退款
     */
    ChannelPaymentRefundResultDto createRefund(PaymentRefundOrder refundOrder);

    /**
     * 关闭第三方支付单
     */
    void closePayment(PaymentOrder paymentOrder);
}
