package com.haoclass.main.interfaces.vo.mq.response;

import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminMqDeadMessageDetailRespVo {

    /**
     * 死信消息ID
     */
    private Long id;

    /**
     * 业务类型：payment.success、payment.failed、payment.expired
     */
    private String bizType;

    /**
     * 业务ID，例如支付单号 paymentNo
     */
    private String bizId;

    /**
     * 来源队列
     */
    private String sourceQueue;

    /**
     * 来源交换机
     */
    private String sourceExchange;

    /**
     * 来源路由键
     */
    private String sourceRoutingKey;

    /**
     * 死信队列
     */
    private String deadQueue;

    /**
     * 死信交换机
     */
    private String deadExchange;

    /**
     * 死信路由键
     */
    private String deadRoutingKey;

    /**
     * 消息体JSON
     */
    private String messageBody;

    /**
     * 消息头JSON
     */
    private String headers;

    /**
     * 进入死信原因
     */
    private String deadReason;

    /**
     * 最后错误信息
     */
    private String lastError;

    /**
     * 处理状态：0待处理 1已重投 2已忽略 3处理失败
     * @see MqDeadMessageStatusEnum
     */
    private MqDeadMessageStatusEnum status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最后重试时间
     */
    private LocalDateTime lastRetryTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}