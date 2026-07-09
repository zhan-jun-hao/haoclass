package com.haoclass.main.interfaces.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haoclass.main.application.service.inner.InnerCourseOrderApplicationService;
import com.haoclass.pay.api.common.contants.MqMessageHeadersKeyConstants;
import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import com.haoclass.pay.api.dto.request.PaymentRefundSuccessRequest;
import com.haoclass.security.trace.TraceContextHolder;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 监听退款成功消息。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefundSuccessListener {

    private final InnerCourseOrderApplicationService innerCourseOrderApplicationService;
    private final ObjectMapper objectMapper;

    /**
     * 监听退款成功队列。
     *
     * @param message 消息元数据和消息体
     * @param channel RabbitMQ通道
     * @throws IOException IO异常
     */
    @RabbitListener(queues = PayRabbitConstants.REFUND_SUCCESS_QUEUE)
    public void onMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        Object traceId = message.getMessageProperties().getHeaders().get(MqMessageHeadersKeyConstants.TRACE_ID);
        TraceContextHolder.getOrCreateTraceId(traceId == null ? null : String.valueOf(traceId));

        PaymentRefundSuccessRequest request = null;
        try {
            request = objectMapper.readValue(getMessageBody(message), PaymentRefundSuccessRequest.class);
            innerCourseOrderApplicationService.handleRefundSuccess(request);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            String refundNo = request == null ? "" : request.getRefundNo();
            log.error("退款成功消息消费失败, refundNo: {}", refundNo, e);
            channel.basicNack(tag, false, false);
        } finally {
            TraceContextHolder.clear();
        }
    }

    private String getMessageBody(Message message) {
        byte[] body = message.getBody();
        if (body == null || body.length == 0) {
            throw new IllegalStateException("退款成功消息体不能为空");
        }
        return new String(body, StandardCharsets.UTF_8);
    }
}
