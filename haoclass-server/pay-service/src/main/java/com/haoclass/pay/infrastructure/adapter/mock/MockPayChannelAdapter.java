package com.haoclass.pay.infrastructure.adapter.mock;

import com.haoclass.pay.infrastructure.adapter.PayChannelAdapter;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentRefundResultDto;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentResultDto;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import org.springframework.stereotype.Component;

/**
 * 模拟支付适配器
 */
@Component
public class MockPayChannelAdapter implements PayChannelAdapter {

    @Override
    public PaymentChannelEnum getChannel() {
        return PaymentChannelEnum.MOCK;
    }

    @Override
    public ChannelPaymentResultDto createPayment(PaymentOrder paymentOrder) {
        return new ChannelPaymentResultDto();
    }

    @Override
    public void closePayment(PaymentOrder paymentOrder) {

    }

    @Override
    public ChannelPaymentRefundResultDto createRefund(PaymentRefundOrder refundOrder) {
        ChannelPaymentRefundResultDto result = new ChannelPaymentRefundResultDto();
        result.setAccepted(true);
        result.setChannelStatus("PROCESSING");
        return result;
    }
}
