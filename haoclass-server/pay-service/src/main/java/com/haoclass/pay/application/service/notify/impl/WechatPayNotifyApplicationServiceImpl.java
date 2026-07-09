package com.haoclass.pay.application.service.notify.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.PaymentRefundSuccessRequest;
import com.haoclass.pay.api.dto.request.PaymentSuccessRequest;
import com.haoclass.pay.application.service.notify.WechatPayNotifyApplicationService;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.domain.service.PaymentRefundOrderService;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import com.wechat.pay.java.service.refund.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * 微信支付回调应用服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "haoclass.pay.wechat", name = "enabled", havingValue = "true")
public class WechatPayNotifyApplicationServiceImpl implements WechatPayNotifyApplicationService {

    private final NotificationParser notificationParser;
    private final PaymentOrderService paymentOrderService;
    private final PaymentRefundOrderService paymentRefundOrderService;
    private final MqMessageService mqMessageService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentNotify(String serial, String signature, String nonce, String timestamp, String body) {
        Transaction transaction = parseTransaction(serial, signature, nonce, timestamp, body);
        if (transaction.getTradeState() != Transaction.TradeStateEnum.SUCCESS) {
            log.info("微信支付回调不是支付成功状态, paymentNo: {}, tradeState: {}",
                    transaction.getOutTradeNo(), transaction.getTradeState());
            return;
        }

        String paymentNo = transaction.getOutTradeNo();
        String thirdTradeNo = transaction.getTransactionId();
        if (!StringUtils.hasText(paymentNo) || !StringUtils.hasText(thirdTradeNo)) {
            throw BusinessException.badRequest("微信支付回调缺少支付单号或微信流水号");
        }

        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo);
        if (paymentOrder.getStatus() == PaymentStatusEnum.SUCCESS) {
            log.info("微信支付回调重复通知，支付单已成功, paymentNo: {}", paymentNo);
            return;
        }

        validatePaymentOrder(paymentOrder, transaction);

        LocalDateTime payTime = parseWechatTime(transaction.getSuccessTime());
        boolean updated = paymentOrderService.tryUpdatePaySuccess(
                paymentOrder.getPaymentNo(),
                paymentOrder.getUserId(),
                thirdTradeNo,
                payTime
        );

        if (!updated) {
            PaymentOrder latest = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo);
            if (latest.getStatus() == PaymentStatusEnum.SUCCESS) {
                log.info("微信支付回调并发重复通知，支付单已成功, paymentNo: {}", paymentNo);
                return;
            }
            throw BusinessException.badRequest("更新微信支付成功状态失败");
        }

        mqMessageService.savePendingMessage(
                MqMessageBizTypeConstants.PAYMENT_SUCCESS,
                paymentNo,
                PayRabbitConstants.PAY_EXCHANGE,
                PayRabbitConstants.PAYMENT_SUCCESS_ROUTING_KEY,
                toJson(buildPaymentSuccessRequest(paymentOrder, thirdTradeNo, payTime))
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundNotify(String serial, String signature, String nonce, String timestamp, String body) {
        RefundNotification notification = parseRefundNotification(serial, signature, nonce, timestamp, body);
        if (notification.getRefundStatus() != Status.SUCCESS) {
            log.info("微信退款回调不是退款成功状态, refundNo: {}, refundStatus: {}",
                    notification.getOutRefundNo(), notification.getRefundStatus());
            return;
        }

        String refundNo = notification.getOutRefundNo();
        String thirdRefundNo = notification.getRefundId();
        if (!StringUtils.hasText(refundNo) || !StringUtils.hasText(thirdRefundNo)) {
            throw BusinessException.badRequest("微信退款回调缺少退款单号或微信退款流水号");
        }

        PaymentRefundOrder refundOrder = paymentRefundOrderService.getPaymentRefundOrderByRefundNo(refundNo);
        if (refundOrder.getStatus() == PaymentRefundStatusEnum.SUCCESS) {
            log.info("微信退款回调重复通知，退款单已成功, refundNo: {}", refundNo);
            return;
        }

        validateRefundOrder(refundOrder, notification);

        LocalDateTime refundTime = parseWechatTime(notification.getSuccessTime());
        boolean updated = paymentRefundOrderService.tryUpdateRefundSuccess(
                refundOrder.getRefundNo(),
                refundOrder.getUserId(),
                thirdRefundNo,
                refundTime
        );

        if (!updated) {
            PaymentRefundOrder latest = paymentRefundOrderService.getPaymentRefundOrderByRefundNo(refundNo);
            if (latest.getStatus() == PaymentRefundStatusEnum.SUCCESS) {
                log.info("微信退款回调并发重复通知，退款单已成功, refundNo: {}", refundNo);
                return;
            }
            throw BusinessException.badRequest("更新微信退款成功状态失败");
        }

        refundOrder.setStatus(PaymentRefundStatusEnum.SUCCESS);
        refundOrder.setThirdRefundNo(thirdRefundNo);
        refundOrder.setRefundTime(refundTime);
        mqMessageService.savePendingMessage(
                MqMessageBizTypeConstants.REFUND_SUCCESS,
                refundNo,
                PayRabbitConstants.PAY_EXCHANGE,
                PayRabbitConstants.REFUND_SUCCESS_ROUTING_KEY,
                toJson(buildPaymentRefundSuccessRequest(refundOrder, thirdRefundNo, refundTime))
        );
    }

    /**
     * 验签并解密微信支付通知
     */
    private Transaction parseTransaction(String serial, String signature, String nonce, String timestamp, String body) {
        return notificationParser
                .parse(buildRequestParam(serial, signature, nonce, timestamp, body), Transaction.class);
    }

    /**
     * 验签并解密微信退款通知
     */
    private RefundNotification parseRefundNotification(String serial, String signature, String nonce,
                                                       String timestamp, String body) {
        return notificationParser.parse(buildRequestParam(serial, signature, nonce, timestamp, body),
                RefundNotification.class);
    }

    /**
     * 构建微信回调验签参数
     */
    private RequestParam buildRequestParam(String serial, String signature, String nonce, String timestamp, String body) {
        if (!StringUtils.hasText(serial)
                || !StringUtils.hasText(signature)
                || !StringUtils.hasText(nonce)
                || !StringUtils.hasText(timestamp)
                || !StringUtils.hasText(body)) {
            throw BusinessException.badRequest("微信回调参数不能为空");
        }

        return new RequestParam.Builder()
                .serialNumber(serial)
                .signature(signature)
                .nonce(nonce)
                .timestamp(timestamp)
                .body(body)
                .build();
    }

    /**
     * 校验微信支付成功回调与本地支付单是否一致。
     */
    private void validatePaymentOrder(PaymentOrder paymentOrder, Transaction transaction) {
        if (paymentOrder.getPayChannel() != PaymentChannelEnum.WECHAT) {
            throw BusinessException.badRequest("支付单不是微信支付渠道");
        }
        if (paymentOrder.getStatus() != PaymentStatusEnum.PENDING) {
            throw BusinessException.badRequest("当前支付单状态不允许支付成功");
        }
        Integer wechatPayAmount = transaction.getAmount() == null ? null : transaction.getAmount().getTotal();
        if (!Objects.equals(paymentOrder.getPayAmount(), wechatPayAmount)) {
            throw BusinessException.badRequest("微信支付回调金额不一致");
        }
    }

    /**
     * 校验微信退款成功回调与本地退款单是否一致。
     */
    private void validateRefundOrder(PaymentRefundOrder refundOrder, RefundNotification notification) {
        if (refundOrder.getPayChannel() != PaymentChannelEnum.WECHAT) {
            throw BusinessException.badRequest("退款单不是微信支付渠道");
        }
        if (refundOrder.getStatus() != PaymentRefundStatusEnum.PENDING
                && refundOrder.getStatus() != PaymentRefundStatusEnum.PROCESSING) {
            throw BusinessException.badRequest("当前退款单状态不允许退款成功");
        }
        if (StringUtils.hasText(notification.getOutTradeNo())
                && !Objects.equals(refundOrder.getPaymentNo(), notification.getOutTradeNo())) {
            throw BusinessException.badRequest("微信退款回调原支付单号不一致");
        }
        if (StringUtils.hasText(notification.getTransactionId())
                && StringUtils.hasText(refundOrder.getThirdTradeNo())
                && !Objects.equals(refundOrder.getThirdTradeNo(), notification.getTransactionId())) {
            throw BusinessException.badRequest("微信退款回调原微信支付流水号不一致");
        }
        Long wechatRefundAmount = notification.getAmount() == null ? null : notification.getAmount().getRefund();
        if (!Objects.equals(refundOrder.getRefundAmount().longValue(), wechatRefundAmount)) {
            throw BusinessException.badRequest("微信退款回调金额不一致");
        }
    }

    /**
     * 解析微信回调时间。
     */
    private LocalDateTime parseWechatTime(String successTime) {
        if (!StringUtils.hasText(successTime)) {
            return LocalDateTime.now();
        }
        return OffsetDateTime.parse(successTime)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 构建退款成功消息体。
     */
    private PaymentRefundSuccessRequest buildPaymentRefundSuccessRequest(PaymentRefundOrder refundOrder,
                                                                         String thirdRefundNo,
                                                                         LocalDateTime refundTime) {
        PaymentRefundSuccessRequest request = new PaymentRefundSuccessRequest();
        request.setBizType(refundOrder.getBizType().getCode());
        request.setBizOrderId(refundOrder.getBizOrderId());
        request.setBizOrderNo(refundOrder.getBizOrderNo());
        request.setPaymentNo(refundOrder.getPaymentNo());
        request.setRefundNo(refundOrder.getRefundNo());
        request.setThirdRefundNo(thirdRefundNo);
        request.setRefundAmount(refundOrder.getRefundAmount());
        request.setPayChannel(refundOrder.getPayChannel().getCode());
        request.setRefundTime(refundTime);
        return request;
    }

    /**
     * 构建支付成功消息体。
     */
    private PaymentSuccessRequest buildPaymentSuccessRequest(PaymentOrder paymentOrder,
                                                             String thirdTradeNo,
                                                             LocalDateTime payTime) {
        PaymentSuccessRequest request = new PaymentSuccessRequest();
        request.setBizType(paymentOrder.getBizType().getCode());
        request.setBizOrderId(paymentOrder.getBizOrderId());
        request.setPaymentNo(paymentOrder.getPaymentNo());
        request.setThirdTradeNo(thirdTradeNo);
        request.setPayAmount(paymentOrder.getPayAmount());
        request.setPayChannel(paymentOrder.getPayChannel().getCode());
        request.setPayTime(payTime);
        return request;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("支付回调消息序列化失败");
        }
    }
}
