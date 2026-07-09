package com.haoclass.pay.infrastructure.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息转换配置
 */
@Configuration
public class RabbitMessageConverterConfig {

    /**
     * 使用JSON格式序列化和反序列化消息
     *
     * @return 消息转换器
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}