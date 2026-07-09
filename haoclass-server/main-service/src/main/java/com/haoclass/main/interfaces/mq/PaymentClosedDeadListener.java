package com.haoclass.main.interfaces.mq;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.main.domain.service.MqDeadMessageService;
import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.pay.api.common.contants.MqMessageBizTypeConstants;
import com.haoclass.pay.api.common.contants.MqMessageHeadersKeyConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.PaymentClosedRequest;
import com.haoclass.security.trace.TraceContextHolder;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 监听支付关闭死信队列。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClosedDeadListener {

    private final MqDeadMessageService mqDeadMessageService;
    private final ObjectMapper objectMapper;

    /**
     * 监听支付关闭死信队列
     *
     * @param message 消息元数据和消息体
     * @param channel RabbitMQ通道
     * @throws IOException IO异常
     */
    @RabbitListener(queues = PayRabbitConstants.PAYMENT_CLOSED_DEAD_QUEUE)
    public void onMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        Object traceId = message.getMessageProperties().getHeaders().get(MqMessageHeadersKeyConstants.TRACE_ID);
        TraceContextHolder.getOrCreateTraceId(traceId == null ? null : String.valueOf(traceId));
        PaymentClosedRequest request = null;
        try {
            String messageBody = getMessageBody(message);
            request = objectMapper.readValue(messageBody, PaymentClosedRequest.class);
            log.error("支付关闭消息进入死信队列, request: {}, headers: {}",
                    request, message.getMessageProperties().getHeaders());

            MqDeadMessage deadMessage = buildMqDeadMessage(request, messageBody, message);
            boolean saved = mqDeadMessageService.save(deadMessage);
            if (!saved) {
                throw new IllegalStateException("保存支付关闭死信消息失败");
            }

            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("处理支付关闭死信消息失败", e);
            channel.basicNack(tag, false, true);
        } finally {
            TraceContextHolder.clear();
        }
    }

    private MqDeadMessage buildMqDeadMessage(PaymentClosedRequest request, String messageBody, Message message) {
        Map<String, Object> headerMap = getHeaderMap(message);
        LocalDateTime now = LocalDateTime.now();

        MqDeadMessage mqDeadMessage = new MqDeadMessage();
        mqDeadMessage.setId(IdUtil.getSnowflakeNextId());
        mqDeadMessage.setBizType(resolveBizType(request));
        mqDeadMessage.setBizId(request == null ? "" : request.getPaymentNo());
        mqDeadMessage.setSourceQueue(getHeaderValue(headerMap,
                MqMessageHeadersKeyConstants.TARGET_QUEUE, getSourceQueueFromXDeath(message)));
        mqDeadMessage.setSourceExchange(getHeaderValue(headerMap,
                MqMessageHeadersKeyConstants.TARGET_EXCHANGE, PayRabbitConstants.PAY_EXCHANGE));
        mqDeadMessage.setSourceRoutingKey(getHeaderValue(headerMap,
                MqMessageHeadersKeyConstants.TARGET_ROUTING_KEY, PayRabbitConstants.PAYMENT_CLOSED_ROUTING_KEY));
        mqDeadMessage.setDeadQueue(PayRabbitConstants.PAYMENT_CLOSED_DEAD_QUEUE);
        mqDeadMessage.setDeadExchange(PayRabbitConstants.PAY_DEAD_EXCHANGE);
        mqDeadMessage.setDeadRoutingKey(PayRabbitConstants.PAYMENT_CLOSED_DEAD_ROUTING_KEY);
        mqDeadMessage.setMessageBody(messageBody);
        mqDeadMessage.setHeaders(toJson(headerMap));
        mqDeadMessage.setDeadReason(getDeadReasonFromXDeath(message));
        mqDeadMessage.setLastError("");
        mqDeadMessage.setStatus(MqDeadMessageStatusEnum.PENDING_PROCESS);
        mqDeadMessage.setRetryCount(0);
        mqDeadMessage.setLastRetryTime(null);
        mqDeadMessage.setCreatedUser(0L);
        mqDeadMessage.setUpdatedUser(0L);
        mqDeadMessage.setCreateTime(now);
        mqDeadMessage.setUpdateTime(now);
        mqDeadMessage.setDeleted(0);
        return mqDeadMessage;
    }

    private String resolveBizType(PaymentClosedRequest request) {
        if (request == null || request.getEventType() == null) {
            return MqMessageBizTypeConstants.PAYMENT_EXPIRED;
        }
        return request.getEventType();
    }

    private Map<String, Object> getHeaderMap(Message message) {
        Map<String, Object> headerMap = message.getMessageProperties().getHeaders();
        Object targetExchange = headerMap.get(MqMessageHeadersKeyConstants.TARGET_EXCHANGE);
        Object targetQueue = headerMap.get(MqMessageHeadersKeyConstants.TARGET_QUEUE);
        Object targetRoutingKey = headerMap.get(MqMessageHeadersKeyConstants.TARGET_ROUTING_KEY);
        Object messageId = headerMap.get(MqMessageHeadersKeyConstants.MESSAGE_ID);
        if (targetExchange == null || targetQueue == null || targetRoutingKey == null || messageId == null) {
            log.warn("支付关闭死信消息缺少投递头信息, headers: {}", headerMap);
        }
        return headerMap;
    }

    private String getHeaderValue(Map<String, Object> headerMap, String key, String defaultValue) {
        Object value = headerMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    private String getMessageBody(Message message) {
        byte[] body = message.getBody();
        if (body == null || body.length == 0) {
            throw new IllegalStateException("死信消息体为空");
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    private String toJson(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("对象转JSON失败, value: {}", value, e);
            return String.valueOf(value);
        }
    }

    private String getSourceQueueFromXDeath(Message message) {
        Map<?, ?> xDeath = getFirstXDeath(message);
        if (xDeath == null) {
            return PayRabbitConstants.PAYMENT_CLOSED_QUEUE;
        }
        Object queue = xDeath.get("queue");
        return queue == null ? PayRabbitConstants.PAYMENT_CLOSED_QUEUE : String.valueOf(queue);
    }

    private String getDeadReasonFromXDeath(Message message) {
        Map<?, ?> xDeath = getFirstXDeath(message);
        if (xDeath == null) {
            return "";
        }
        Object reason = xDeath.get("reason");
        return reason == null ? "" : String.valueOf(reason);
    }

    private Map<?, ?> getFirstXDeath(Message message) {
        Object xDeathObj = message.getMessageProperties().getHeaders().get("x-death");
        if (!(xDeathObj instanceof List<?> xDeathList) || xDeathList.isEmpty()) {
            return null;
        }
        Object first = xDeathList.get(0);
        if (!(first instanceof Map<?, ?> xDeath)) {
            return null;
        }
        return xDeath;
    }
}
