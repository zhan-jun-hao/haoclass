package com.haoclass.pay.application.service.notify.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.PaymentRefundSuccessRequest;
import com.haoclass.pay.application.service.notify.PaymentRefundNotifyApplicationService;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.domain.service.PaymentRefundOrderService;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 退款回调应用服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRefundNotifyApplicationServiceImpl implements PaymentRefundNotifyApplicationService {

    private final PaymentRefundOrderService paymentRefundOrderService;
    private final MqMessageService mqMessageService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleMockRefundSuccess(String refundNo, String thirdRefundNo, LocalDateTime refundTime) {
        if (!StringUtils.hasText(refundNo)) {
            throw BusinessException.badRequest("退款单号不能为空");
        }

        PaymentRefundOrder refundOrder = paymentRefundOrderService.getPaymentRefundOrderByRefundNo(refundNo);
        if (refundOrder.getStatus() == PaymentRefundStatusEnum.SUCCESS) {
            log.info("模拟退款回调重复通知，退款单已成功, refundNo: {}", refundNo);
            return;
        }

        String finalThirdRefundNo = StringUtils.hasText(thirdRefundNo)
                ? thirdRefundNo
                : "MOCK-REFUND-" + refundNo;
        LocalDateTime finalRefundTime = refundTime == null ? LocalDateTime.now() : refundTime;

        boolean updated = paymentRefundOrderService.tryUpdateRefundSuccess(
                refundOrder.getRefundNo(),
                refundOrder.getUserId(),
                finalThirdRefundNo,
                finalRefundTime
        );

        if (!updated) {
            PaymentRefundOrder latest = paymentRefundOrderService.getPaymentRefundOrderByRefundNo(refundNo);
            if (latest.getStatus() == PaymentRefundStatusEnum.SUCCESS) {
                log.info("模拟退款回调并发重复通知，退款单已成功, refundNo: {}", refundNo);
                return;
            }
            throw BusinessException.badRequest("更新退款成功状态失败");
        }

        refundOrder.setStatus(PaymentRefundStatusEnum.SUCCESS);
        refundOrder.setThirdRefundNo(finalThirdRefundNo);
        refundOrder.setRefundTime(finalRefundTime);
        mqMessageService.savePendingMessage(
                MqMessageBizTypeConstants.REFUND_SUCCESS,
                refundOrder.getRefundNo(),
                PayRabbitConstants.PAY_EXCHANGE,
                PayRabbitConstants.REFUND_SUCCESS_ROUTING_KEY,
                toJson(buildRefundSuccessRequest(refundOrder, finalThirdRefundNo, finalRefundTime))
        );
    }

    /**
     * 构建退款成功消息体。
     */
    private PaymentRefundSuccessRequest buildRefundSuccessRequest(PaymentRefundOrder refundOrder,
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

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("退款成功消息序列化失败");
        }
    }
}
