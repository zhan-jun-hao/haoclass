package com.haoclass.main.infrastructure.mq;

import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.trace.TraceConstants;
import com.haoclass.main.domain.service.MqDeadMessageService;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.pay.api.common.contants.MqMessageHeadersKeyConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
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
 * 死信消息消息生产者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeadMessageRetryPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Set<Long> notFindRoutingQueueIds = ConcurrentHashMap.newKeySet();
    private final MqDeadMessageService mqDeadMessageService;

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
            mqDeadMessageService.markFailed(messageId, "交换机里队列路由失败: " + messageId);
        });
        // 2.交换机回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData == null) {
                return;
            }
            Long messageId = Long.valueOf(correlationData.getId());
            if (ack) {
                if (notFindRoutingQueueIds.remove(messageId)) {
                    log.info("交换机接收了:{} 但是没路由到对应队列", messageId);
                    return;
                }
                mqDeadMessageService.markStatusRetry(messageId);
            } else {
                mqDeadMessageService.markFailed(messageId, "交换机接收失败: " + messageId);
            }
        });
    }

    public void send(MqDeadMessage message) {
        try {
            String messageBody = message.getMessageBody();
            CorrelationData correlationData = new CorrelationData(String.valueOf(message.getId()));
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding(StandardCharsets.UTF_8.name());
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            messageProperties.setHeader(MqMessageHeadersKeyConstants.MESSAGE_ID, message.getId());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_EXCHANGE, message.getSourceExchange());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_ROUTING_KEY, message.getSourceRoutingKey());
            messageProperties.setHeader(MqMessageHeadersKeyConstants.TARGET_QUEUE, resolveTargetQueue(message.getSourceRoutingKey()));
            messageProperties.setHeader(TraceConstants.TRACE_ID_HEADER, TraceContextHolder.getOrCreateTraceId());

            Message amqpMessage = new Message(messageBody.getBytes(StandardCharsets.UTF_8), messageProperties);
            rabbitTemplate.send(message.getSourceExchange(), message.getSourceRoutingKey(), amqpMessage, correlationData);
        } catch (Exception e) {
            mqDeadMessageService.markFailed(message.getId(), "人工重投信息失败: " + message.getId());
            throw BusinessException.badRequest("人工重投消息失败");
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
        return "";
    }

}

