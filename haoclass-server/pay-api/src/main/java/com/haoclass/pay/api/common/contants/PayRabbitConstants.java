package com.haoclass.pay.api.common.contants;

public interface PayRabbitConstants {

    /**
     * 消息发送-->交换机-->根据routingKey将信息-->放入队列
     */
    String PAY_EXCHANGE = "haoclass.pay.exchange";
    // 支付成功
    String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success.course-order";
    String PAYMENT_SUCCESS_QUEUE = "haoclass.main.payment.success.queue";
    // 支付超时
    String PAYMENT_EXPIRED_ROUTING_KEY = "payment.expired.course-order";
    String PAYMENT_EXPIRED_QUEUE = "haoclass.main.payment.expired.queue";
    String PAYMENT_CLOSED_ROUTING_KEY = "payment.closed.course-order";
    String PAYMENT_CLOSED_QUEUE = "haoclass.main.payment.closed.queue";
    // 退款成功
    String REFUND_SUCCESS_ROUTING_KEY = "refund.success.course-order";
    String REFUND_SUCCESS_QUEUE = "haoclass.main.refund.success.queue";

    String PAY_DEAD_EXCHANGE = "haoclass.pay.dead.exchange";
    String PAYMENT_SUCCESS_DEAD_ROUTING_KEY = "payment.success.course-order.dead";
    String PAYMENT_SUCCESS_DEAD_QUEUE = "haoclass.main.payment.success.dead.queue";
    String PAYMENT_CLOSED_DEAD_ROUTING_KEY = "payment.closed.course-order.dead";
    String PAYMENT_CLOSED_DEAD_QUEUE = "haoclass.main.payment.closed.dead.queue";
    String REFUND_SUCCESS_DEAD_ROUTING_KEY = "refund.success.course-order.dead";
    String REFUND_SUCCESS_DEAD_QUEUE = "haoclass.main.refund.success.dead.queue";
}
