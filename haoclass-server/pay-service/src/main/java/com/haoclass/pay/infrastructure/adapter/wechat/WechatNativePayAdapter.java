package com.haoclass.pay.infrastructure.adapter.wechat;

import com.haoclass.pay.infrastructure.adapter.PayChannelAdapter;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentRefundResultDto;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentResultDto;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.config.WechatPayProperties;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 微信Native支付适配器
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "haoclass.pay.wechat", name = "enabled", havingValue = "true")
public class WechatNativePayAdapter implements PayChannelAdapter {

    private final WechatPayProperties properties;
    private final NativePayService nativePayService;
    private final RefundService refundService;

    @Override
    public PaymentChannelEnum getChannel() {
        return PaymentChannelEnum.WECHAT;
    }

    @Override
    public ChannelPaymentResultDto createPayment(PaymentOrder paymentOrder) {
        PrepayRequest request = new PrepayRequest();
        request.setAppid(properties.getAppId());
        request.setMchid(properties.getMchId());
        request.setDescription(paymentOrder.getSubject());
        request.setOutTradeNo(paymentOrder.getPaymentNo());
        request.setNotifyUrl(properties.getNotifyUrl());

        Amount amount = new Amount();
        amount.setTotal(paymentOrder.getPayAmount());
        amount.setCurrency(paymentOrder.getCurrency());
        request.setAmount(amount);
        // 微信支付要用RFC3339时间
        request.setTimeExpire(paymentOrder.getExpireTime()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        PrepayResponse response = nativePayService.prepay(request);

        ChannelPaymentResultDto result = new ChannelPaymentResultDto();
        result.setCodeUrl(response.getCodeUrl());
        return result;
    }

    @Override
    public void closePayment(PaymentOrder paymentOrder) {
        CloseOrderRequest request = new CloseOrderRequest();
        request.setOutTradeNo(paymentOrder.getPaymentNo());
        request.setMchid(properties.getMchId());
        nativePayService.closeOrder(request);
    }

    @Override
    public ChannelPaymentRefundResultDto createRefund(PaymentRefundOrder refundOrder) {
        CreateRequest request = new CreateRequest();
        request.setTransactionId(refundOrder.getThirdTradeNo());
        request.setOutRefundNo(refundOrder.getRefundNo());
        request.setReason(refundOrder.getRefundReason());
        request.setNotifyUrl(properties.getRefundNotifyUrl());

        AmountReq amount = new AmountReq();
        amount.setRefund(refundOrder.getRefundAmount().longValue());
        amount.setTotal(refundOrder.getPayAmount().longValue());
        amount.setCurrency(refundOrder.getCurrency());
        request.setAmount(amount);

        Refund refund = refundService.create(request);
        ChannelPaymentRefundResultDto result = new ChannelPaymentRefundResultDto();
        result.setAccepted(true);
        result.setChannelStatus(refund.getStatus() == null ? null : refund.getStatus().name());
        return result;
    }
}
