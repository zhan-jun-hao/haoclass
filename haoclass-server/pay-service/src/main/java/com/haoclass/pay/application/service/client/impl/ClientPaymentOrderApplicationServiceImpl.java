
package com.haoclass.pay.application.service.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.PaymentClosedRequest;
import com.haoclass.pay.api.dto.request.PaymentSuccessRequest;
import com.haoclass.pay.application.converter.client.ClientPaymentOrderConverter;
import com.haoclass.pay.application.service.client.ClientPaymentOrderApplicationService;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.client.response.ClientPaymentOrderDetailVo;
import com.haoclass.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * C端支付订单应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class ClientPaymentOrderApplicationServiceImpl implements ClientPaymentOrderApplicationService {

    private final PaymentOrderService paymentOrderService;
    private final MqMessageService mqMessageService;
    private final ObjectMapper objectMapper;

    @Override
    public ClientPaymentOrderDetailVo getPaymentOrderDetail(String paymentNo) {
        Long userId = UserContextHolder.getRequiredUserId();
        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo, userId);
        return ClientPaymentOrderConverter.INSTANCE.poToDetailVo(paymentOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockPaySuccess(String paymentNo) {
        Long userId = UserContextHolder.getUserId();
        // 1.查询订单
        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo, userId);
        // 2.只有模拟支付允许手动支付成功
        if(paymentOrder.getPayChannel() != PaymentChannelEnum.MOCK) {
            throw BusinessException.badRequest("非模拟支付不允许手动支付成功");
        }
        // 3.如果订单已经SUCCESS直接返回成功
        if(paymentOrder.getStatus() == PaymentStatusEnum.SUCCESS) {
            return;
        }
        // 4.只有PENDING状态的订单才支持支付
        if(paymentOrder.getStatus() != PaymentStatusEnum.PENDING) {
            throw BusinessException.badRequest("当前支付单状态不允许支付");
        }
        // 5.模拟支付
        // TODO:具体支付会返回一个第三方流水号
        String thirdTradeNo = "MOCK-PAY" + paymentNo;
        LocalDateTime payTime = LocalDateTime.now();
        // 6.更新支付订单状态 数据库唯一键索引兜底
        boolean updated = paymentOrderService.tryUpdatePaySuccess(paymentOrder.getPaymentNo(), userId
                , thirdTradeNo, payTime);
        if(!updated) {
            PaymentOrder order = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo, userId);
            if(order.getStatus() == PaymentStatusEnum.SUCCESS) {
                return;
            } else {
                paymentOrderService.updatePaySuccess(paymentOrder.getPaymentNo(), userId
                        , thirdTradeNo, payTime);
            }
        }
        PaymentSuccessRequest request = new PaymentSuccessRequest();
        request.setBizType(paymentOrder.getBizType().getCode());
        request.setBizOrderId(paymentOrder.getBizOrderId());
        request.setPaymentNo(paymentOrder.getPaymentNo());
        request.setThirdTradeNo(thirdTradeNo);
        request.setPayAmount(paymentOrder.getPayAmount());
        request.setPayChannel(paymentOrder.getPayChannel().getCode());
        request.setPayTime(payTime);
        // 7.支付成功后通知main-service履约
        // 写入本地消息表
        mqMessageService.savePendingMessage(MqMessageBizTypeConstants.PAYMENT_SUCCESS, paymentNo
                , PayRabbitConstants.PAY_EXCHANGE, PayRabbitConstants.PAYMENT_SUCCESS_ROUTING_KEY
                , toJson(request));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockPayFail(String paymentNo) {
        Long userId = UserContextHolder.getRequiredUserId();
        // 1.查询订单
        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo, userId);
        // 2.只有模拟支付允许手动支付失败
        if(paymentOrder.getPayChannel() != PaymentChannelEnum.MOCK) {
            throw BusinessException.badRequest("非模拟支付不允许手动支付失败");
        }
        // 3.如果订单已经FAILED直接返回成功
        if(paymentOrder.getStatus() == PaymentStatusEnum.FAILED) {
            return;
        }
        // 4.只有PENDING状态的订单才支持更新状态为FAILED
        if(paymentOrder.getStatus() != PaymentStatusEnum.PENDING) {
            throw BusinessException.badRequest("当前支付单状态不允许支付失败");
        }
        LocalDateTime payTime = LocalDateTime.now();
        paymentOrderService.updatePayFailed(paymentOrder.getPaymentNo(), userId, payTime);
        PaymentClosedRequest request = buildPaymentClosedRequest(paymentOrder, payTime, "支付失败");
        mqMessageService.savePendingMessage(MqMessageBizTypeConstants.PAYMENT_FAILED, paymentNo,
                PayRabbitConstants.PAY_EXCHANGE, PayRabbitConstants.PAYMENT_CLOSED_ROUTING_KEY,
                toJson(request));
    }

    private PaymentClosedRequest buildPaymentClosedRequest(PaymentOrder paymentOrder, LocalDateTime closeTime,
                                                           String closeReason) {
        PaymentClosedRequest request = new PaymentClosedRequest();
        request.setEventType(MqMessageBizTypeConstants.PAYMENT_FAILED);
        request.setBizType(paymentOrder.getBizType().getCode());
        request.setBizOrderId(paymentOrder.getBizOrderId());
        request.setBizOrderNo(paymentOrder.getBizOrderNo());
        request.setPaymentNo(paymentOrder.getPaymentNo());
        request.setPayAmount(paymentOrder.getPayAmount());
        request.setPayChannel(paymentOrder.getPayChannel().getCode());
        request.setPaymentStatus(PaymentStatusEnum.FAILED.getCode());
        request.setCloseTime(closeTime);
        request.setCloseReason(closeReason);
        return request;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("支付消息序列化失败");
        }
    }
}
