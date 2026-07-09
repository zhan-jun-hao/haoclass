package com.haoclass.pay.infrastructure.mq;

import cn.hutool.core.util.IdUtil;
import com.haoclass.common.trace.TraceConstants;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.api.common.contants.MqMessageHeadersKeyConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.infrastructure.common.enums.MqMessageStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.security.context.UserContextHolder;
import com.haoclass.security.trace.TraceContextHolder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付成功消息生产者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MqMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MqMessageService mqMessageService;
    private final Set<Long> notFindRoutingQueueIds = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        // 1.交换机对应路由回调
        rabbitTemplate.setReturnsCallback(returned -> {
            Object messageIdObj = returned.getMessage()
                    .getMessageProperties()
                    .getHeaders()
                    .get(MqMessageHeadersKeyConstants.MESSAGE_ID);
            if (messageIdObj == null) {
                return;
            }
            Long messageId = Long.valueOf(String.valueOf(messageIdObj));
            notFindRoutingQueueIds.add(messageId);
            mqMessageService.markFailedAndRetryLater(messageId, "消息路由失败: " + returned.getReplyText());
        });
        // 2.交换机回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData == null) {
                return;
            }
            Long messageId = Long.valueOf(correlationData.getId());
            if(ack) {
                if(notFindRoutingQueueIds.remove(messageId)) {
                    log.info("交换机接收了:{} 但是没路由到对应队列", messageId);
                    return;
                }
                mqMessageService.markStatusSend(messageId);
            } else {
                mqMessageService.markFailedAndRetryLater(messageId, cause);
            }
        });
    }

    public void send(MqMessage message) {
        try {
            String messageBody = message.getMessageBody();
            CorrelationData correlationData = new CorrelationData(String.valueOf(message.getId()));
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding(StandardCharsets.UTF_8.name());
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            messageProperties.setHeader(MqMessageHeadersKeyConstants.MESSAGE_ID, message.getId());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_EXCHANGE, message.getExchangeName());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_ROUTING_KEY, message.getRoutingKey());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_QUEUE, resolveTargetQueue(message.getRoutingKey()));
            messageProperties.setHeader(TraceConstants.TRACE_ID_HEADER, TraceContextHolder.getOrCreateTraceId());

            Message amqpMessage = new Message(messageBody.getBytes(StandardCharsets.UTF_8), messageProperties);
            rabbitTemplate.send(message.getExchangeName(), message.getRoutingKey(), amqpMessage, correlationData);

        } catch (Exception e) {
            mqMessageService.markFailedAndRetryLater(message.getId(), e.getMessage());
            // 不抛异常 后面的消息继续发
            log.error("发送消息失败, messageId: {}", message.getId(), e);
        }
    }

    private String resolveTargetQueue(String routingKey) {
        if (PayRabbitConstants.PAYMENT_SUCCESS_ROUTING_KEY.equals(routingKey)) {
            return PayRabbitConstants.PAYMENT_SUCCESS_QUEUE;
        }
        if (PayRabbitConstants.PAYMENT_CLOSED_ROUTING_KEY.equals(routingKey)
                || PayRabbitConstants.PAYMENT_EXPIRED_ROUTING_KEY.equals(routingKey)) {
            return PayRabbitConstants.PAYMENT_CLOSED_QUEUE;
        }
        if (PayRabbitConstants.REFUND_SUCCESS_ROUTING_KEY.equals(routingKey)) {
            return PayRabbitConstants.REFUND_SUCCESS_QUEUE;
        }
        return "";
    }

    /**
     * 消息构造器
     * bizType
     * @see MqMessageBizTypeConstants
     *
     * exchangeName routingKey
     * @see PayRabbitConstants
     */
    public MqMessage buildPaymentMessage(PaymentOrder paymentOrder, String bizType, String exchangeName
            , String routingKey, String messageBody) {
        MqMessage mq = new MqMessage();
        mq.setId(IdUtil.getSnowflakeNextId());
        mq.setBizType(bizType);
        mq.setBizId(paymentOrder.getPaymentNo());
        mq.setExchangeName(exchangeName);
        mq.setRoutingKey(routingKey);
        mq.setMessageBody(messageBody);
        mq.setStatus(MqMessageStatusEnum.PENDING_SEND);
        mq.setRetryCount(0);
        mq.setNextRetryTime(LocalDateTime.now());
        mq.setCreatedUser(UserContextHolder.getUserId() == null ? 0L : UserContextHolder.getUserId());
        mq.setUpdatedUser(UserContextHolder.getUserId() == null ? 0L : UserContextHolder.getUserId());
        mq.setUpdateTime(LocalDateTime.now());
        mq.setCreateTime(LocalDateTime.now());
        mq.setDeleted(0);
        return mq;
    }
}
