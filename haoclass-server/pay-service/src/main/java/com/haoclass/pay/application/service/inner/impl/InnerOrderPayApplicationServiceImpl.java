package com.haoclass.pay.application.service.inner.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.CreatePaymentRequest;
import com.haoclass.pay.api.dto.request.PaymentClosedRequest;
import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.application.converter.inner.InnerOrderPaymentConverter;
import com.haoclass.pay.application.service.inner.InnerOrderPayApplicationService;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.infrastructure.adapter.PayChannelAdapter;
import com.haoclass.pay.infrastructure.adapter.PayChannelAdapterFactory;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentResultDto;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.mq.MqMessagePublisher;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.wechat.pay.java.service.payments.h5.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InnerOrderPayApplicationServiceImpl implements InnerOrderPayApplicationService {

    private final PaymentOrderService paymentOrderService;
    private final RedissonClient redissonClient;
    private final MqMessageService mqMessageService;
    private final MqMessagePublisher mqMessagePublisher;
    private final ObjectMapper objectMapper;
    private final PayChannelAdapterFactory payChannelAdapterFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        PaymentBizTypeEnum bizType = validateRequest(request);

        PaymentOrder existing = paymentOrderService.findByBizOrder(bizType, request.getBizOrderId());
        if (existing != null) {
            validateExistingPaymentConsistency(existing, request);
            if (!needCreateChannelPayment(existing)) {
                return converter(existing);
            }
        }

        String lockKey = "pay:create:" + request.getBizType() + ":" + request.getBizOrderId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw BusinessException.badRequest("支付请求处理中，请稍后重试");
            }
            try {
                existing = paymentOrderService.findByBizOrder(bizType, request.getBizOrderId());
                if (existing != null) {
                    validateExistingPaymentConsistency(existing, request);
                    createChannelPaymentIfNecessary(existing);
                    return converter(existing);
                }

                PaymentOrder paymentOrder = buildPayOrder(request, bizType);
                paymentOrderService.addPaymentOrder(paymentOrder);
                createChannelPaymentIfNecessary(paymentOrder);
                return converter(paymentOrder);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("创建支付单被中断");
        }
    }

    /**
     * 是否需要创建第三方支付
     */
    private boolean needCreateChannelPayment(PaymentOrder paymentOrder) {
        return paymentOrder.getStatus() == PaymentStatusEnum.PENDING
                && paymentOrder.getPayChannel() != PaymentChannelEnum.MOCK
                && !StringUtils.hasText(paymentOrder.getCodeUrl());
    }

    /**
     * 必要时创建第三方支付并保存支付二维码
     */
    private void createChannelPaymentIfNecessary(PaymentOrder paymentOrder) {
        if (!needCreateChannelPayment(paymentOrder)) {
            return;
        }

        PayChannelAdapter adapter = payChannelAdapterFactory.getAdapter(paymentOrder.getPayChannel());
        ChannelPaymentResultDto result = adapter.createPayment(paymentOrder);
        if (result == null || !StringUtils.hasText(result.getCodeUrl())) {
            return;
        }

        paymentOrderService.updateCodeUrl(paymentOrder.getPaymentNo(), paymentOrder.getUserId(), result.getCodeUrl());
        paymentOrder.setCodeUrl(result.getCodeUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeExpiredPendingOrders(int batchSize) {
        // 1.批量查询超时支付订单
        List<PaymentOrder> expiredOrders = paymentOrderService.findExpiredOrders(batchSize);
        if (expiredOrders.isEmpty()) {
            return;
        }
        // 2.逐条关闭订单
        List<MqMessage> messages = new ArrayList<>();
        LocalDateTime closeTime = LocalDateTime.now();
        for (PaymentOrder expiredOrder : expiredOrders) {
            PayChannelAdapter adapter = payChannelAdapterFactory.getAdapter(expiredOrder.getPayChannel());
            adapter.closePayment(expiredOrder);
            boolean closed = paymentOrderService.updateExpiredOrder(expiredOrder.getId(), closeTime);
            if (!closed) {
                continue;
            }
            messages.add(mqMessagePublisher.buildPaymentMessage(
                    expiredOrder,
                    MqMessageBizTypeConstants.PAYMENT_EXPIRED,
                    PayRabbitConstants.PAY_EXCHANGE,
                    PayRabbitConstants.PAYMENT_CLOSED_ROUTING_KEY,
                    toJson(buildPaymentClosedRequest(expiredOrder, closeTime))
            ));
        }
        mqMessageService.savePendingMessages(messages);
    }

    private PaymentClosedRequest buildPaymentClosedRequest(PaymentOrder paymentOrder, LocalDateTime closeTime) {
        PaymentClosedRequest request = new PaymentClosedRequest();
        request.setEventType(MqMessageBizTypeConstants.PAYMENT_EXPIRED);
        request.setBizType(paymentOrder.getBizType().getCode());
        request.setBizOrderId(paymentOrder.getBizOrderId());
        request.setBizOrderNo(paymentOrder.getBizOrderNo());
        request.setPaymentNo(paymentOrder.getPaymentNo());
        request.setPayAmount(paymentOrder.getPayAmount());
        request.setPayChannel(paymentOrder.getPayChannel().getCode());
        request.setPaymentStatus(PaymentStatusEnum.CLOSED.getCode());
        request.setCloseTime(closeTime);
        request.setCloseReason("支付超时");
        return request;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("支付关闭消息序列化失败");
        }
    }

    /**
     * 校验创建支付单请求
     *
     * @param request 创建支付单请求
     * @return 支付业务类型
     */
    private PaymentBizTypeEnum validateRequest(CreatePaymentRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("创建支付单请求不能为空");
        }
        PaymentBizTypeEnum bizType = PaymentBizTypeEnum.of(request.getBizType());
        if (bizType == null) {
            throw BusinessException.badRequest("支付业务类型错误");
        }
        if (request.getBizOrderId() == null) {
            throw BusinessException.badRequest("业务订单ID不能为空");
        }
        if (!StringUtils.hasText(request.getBizOrderNo())) {
            throw BusinessException.badRequest("业务订单号不能为空");
        }
        if (request.getUserId() == null) {
            throw BusinessException.badRequest("支付用户ID不能为空");
        }
        if (!StringUtils.hasText(request.getSubject())) {
            throw BusinessException.badRequest("支付标题不能为空");
        }
        if (request.getPayAmount() == null || request.getPayAmount() <= 0) {
            throw BusinessException.badRequest("支付金额必须大于0");
        }
        if (PaymentChannelEnum.of(request.getPayChannel()) == null) {
            throw BusinessException.badRequest("支付渠道错误");
        }
        if (request.getExpireTime() == null || !request.getExpireTime().isAfter(LocalDateTime.now())) {
            throw BusinessException.badRequest("支付单过期时间必须晚于当前时间");
        }

        return bizType;
    }

    /**
     * 校验重复创建请求与已有支付单的核心数据是否一致。
     *
     * @param existing 已有支付单
     * @param request  创建支付单请求
     */
    private void validateExistingPaymentConsistency(PaymentOrder existing, CreatePaymentRequest request) {
        if (!Objects.equals(existing.getUserId(), request.getUserId())) {
            throw BusinessException.badRequest("支付用户与已有支付单不一致");
        }
        if (!Objects.equals(existing.getBizOrderNo(), request.getBizOrderNo())) {
            throw BusinessException.badRequest("业务订单号与已有支付单不一致");
        }
        if (!Objects.equals(existing.getPayAmount(), request.getPayAmount())) {
            throw BusinessException.badRequest("支付金额与已有支付单不一致");
        }
        if (!Objects.equals(existing.getPayChannel(), PaymentChannelEnum.of(request.getPayChannel()))) {
            throw BusinessException.badRequest("支付渠道与已有支付单不一致");
        }
        if (existing.getStatus() == PaymentStatusEnum.PENDING) {
            return;
        }
        if (existing.getStatus() == PaymentStatusEnum.SUCCESS) {
            throw BusinessException.badRequest("该订单已支付成功，无需重复创建支付单");
        }
        throw BusinessException.badRequest("已有支付单状态为：" + existing.getStatus().getDesc() + "，请重新创建课程订单");
    }

    private CreatePaymentResponse converter(PaymentOrder paymentOrder) {
        CreatePaymentResponse createPaymentResponse = InnerOrderPaymentConverter.INSTANCE.poToInnerResp(paymentOrder);
        createPaymentResponse.setStatus(paymentOrder.getStatus().getCode());
        createPaymentResponse.setPayChannel(paymentOrder.getPayChannel().getCode());
        return createPaymentResponse;
    }

    private PaymentOrder buildPayOrder(CreatePaymentRequest request, PaymentBizTypeEnum bizType) {
        LocalDateTime now = LocalDateTime.now();
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setId(IdUtil.getSnowflakeNextId());
        paymentOrder.setPaymentNo(generatePaymentNo(paymentOrder.getId()));
        paymentOrder.setBizType(bizType);
        paymentOrder.setBizOrderId(request.getBizOrderId());
        paymentOrder.setBizOrderNo(request.getBizOrderNo());
        paymentOrder.setUserId(request.getUserId());
        paymentOrder.setSubject(request.getSubject());
        paymentOrder.setPayAmount(request.getPayAmount());
        paymentOrder.setCurrency("CNY");
        paymentOrder.setPayChannel(PaymentChannelEnum.of(request.getPayChannel()));
        paymentOrder.setStatus(PaymentStatusEnum.PENDING);
        paymentOrder.setExpireTime(request.getExpireTime());
        paymentOrder.setCreateTime(now);
        paymentOrder.setUpdateTime(now);
        paymentOrder.setCreatedUser(request.getUserId());
        paymentOrder.setUpdatedUser(request.getUserId());
        return paymentOrder;
    }

    public String generatePaymentNo(Long paymentOrderId) {
        // 时间戳 + 随机数
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return "PAY" + timestamp + paymentOrderId;
    }

}
