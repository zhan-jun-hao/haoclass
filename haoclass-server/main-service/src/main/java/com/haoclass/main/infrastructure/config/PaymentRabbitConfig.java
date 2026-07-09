package com.haoclass.main.infrastructure.config;

import com.haoclass.pay.api.common.contants.PayRabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMq配置
 */
@Configuration
public class PaymentRabbitConfig {

    /**
     * 创建支付交换机
     *
     * @return
     */
    @Bean
    public DirectExchange payExchange() {
        return new DirectExchange(PayRabbitConstants.PAY_EXCHANGE,
                true,
                false);
    }

    /**
     * 创建支付成功队列
     *
     * @return
     */
    @Bean
    public Queue paymentSuccessQueue() {
        return QueueBuilder.durable(PayRabbitConstants.PAYMENT_SUCCESS_QUEUE)
                .deadLetterExchange(PayRabbitConstants.PAY_DEAD_EXCHANGE)
                .deadLetterRoutingKey(PayRabbitConstants.PAYMENT_SUCCESS_DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 把支付队列绑定至支付交换机
     *
     * @return
     */
    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(paymentSuccessQueue())
                .to(payExchange())
                .with(PayRabbitConstants.PAYMENT_SUCCESS_ROUTING_KEY);
    }

    /**
     * 创建支付关闭队列。
     *
     * @return 支付关闭队列
     */
    @Bean
    public Queue paymentClosedQueue() {
        return QueueBuilder.durable(PayRabbitConstants.PAYMENT_CLOSED_QUEUE)
                .deadLetterExchange(PayRabbitConstants.PAY_DEAD_EXCHANGE)
                .deadLetterRoutingKey(PayRabbitConstants.PAYMENT_CLOSED_DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 把支付关闭队列绑定到支付交换机。
     *
     * @return 绑定关系
     */
    @Bean
    public Binding paymentClosedBinding() {
        return BindingBuilder.bind(paymentClosedQueue())
                .to(payExchange())
                .with(PayRabbitConstants.PAYMENT_CLOSED_ROUTING_KEY);
    }

    /**
     * 创建退款成功队列。
     *
     * @return 退款成功队列
     */
    @Bean
    public Queue refundSuccessQueue() {
        return QueueBuilder.durable(PayRabbitConstants.REFUND_SUCCESS_QUEUE)
                .deadLetterExchange(PayRabbitConstants.PAY_DEAD_EXCHANGE)
                .deadLetterRoutingKey(PayRabbitConstants.REFUND_SUCCESS_DEAD_ROUTING_KEY)
                .build();
    }

    /**
     * 把退款成功队列绑定到支付交换机。
     *
     * @return 绑定关系
     */
    @Bean
    public Binding refundSuccessBinding() {
        return BindingBuilder.bind(refundSuccessQueue())
                .to(payExchange())
                .with(PayRabbitConstants.REFUND_SUCCESS_ROUTING_KEY);
    }

    /**
     * 支付失败交换机
     *
     * @return
     */
    @Bean
    public DirectExchange payDeadExchange() {
        return new DirectExchange(PayRabbitConstants.PAY_DEAD_EXCHANGE, true, false);
    }

    /**
     * 生成死信队列
     *
     * @return
     */
    @Bean
    public Queue paymentSuccessDeadQueue() {
        return QueueBuilder.durable(PayRabbitConstants.PAYMENT_SUCCESS_DEAD_QUEUE)
                .build();
    }

    /**
     * 将支付失败交换机和死信队列绑定
     *
     * @return
     */
    @Bean
    public Binding paymentSuccessDeadBinding() {
        return BindingBuilder.bind(paymentSuccessDeadQueue())
                .to(payDeadExchange())
                .with(PayRabbitConstants.PAYMENT_SUCCESS_DEAD_ROUTING_KEY);
    }

    /**
     * 创建支付关闭死信队列。
     *
     * @return 支付关闭死信队列
     */
    @Bean
    public Queue paymentClosedDeadQueue() {
        return QueueBuilder.durable(PayRabbitConstants.PAYMENT_CLOSED_DEAD_QUEUE)
                .build();
    }

    /**
     * 把支付关闭死信队列绑定到死信交换机。
     *
     * @return 绑定关系
     */
    @Bean
    public Binding paymentClosedDeadBinding() {
        return BindingBuilder.bind(paymentClosedDeadQueue())
                .to(payDeadExchange())
                .with(PayRabbitConstants.PAYMENT_CLOSED_DEAD_ROUTING_KEY);
    }

    /**
     * 创建退款成功死信队列。
     *
     * @return 退款成功死信队列
     */
    @Bean
    public Queue refundSuccessDeadQueue() {
        return QueueBuilder.durable(PayRabbitConstants.REFUND_SUCCESS_DEAD_QUEUE)
                .build();
    }

    /**
     * 把退款成功死信队列绑定到死信交换机。
     *
     * @return 绑定关系
     */
    @Bean
    public Binding refundSuccessDeadBinding() {
        return BindingBuilder.bind(refundSuccessDeadQueue())
                .to(payDeadExchange())
                .with(PayRabbitConstants.REFUND_SUCCESS_DEAD_ROUTING_KEY);
    }
}
