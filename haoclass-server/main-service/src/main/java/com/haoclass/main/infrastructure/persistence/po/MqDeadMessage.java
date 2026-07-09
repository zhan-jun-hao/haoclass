package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * MQ死信消息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mq_dead_message")
public class MqDeadMessage extends BaseEntity {

    /**
     * 业务类型，例如：PAYMENT_SUCCESS
     */
    @TableField("bizType")
    private String bizType;

    /**
     * 业务ID，例如：paymentNo
     */
    @TableField("bizId")
    private String bizId;

    /**
     * 原始业务队列名称
     */
    @TableField("sourceQueue")
    private String sourceQueue;

    /**
     * 原始业务交换机名称
     */
    @TableField("sourceExchange")
    private String sourceExchange;

    /**
     * 原始业务路由键
     */
    @TableField("sourceRoutingKey")
    private String sourceRoutingKey;

    /**
     * 死信队列名称
     */
    @TableField("deadQueue")
    private String deadQueue;

    /**
     * 死信交换机名称
     */
    @TableField("deadExchange")
    private String deadExchange;

    /**
     * 死信路由键
     */
    @TableField("deadRoutingKey")
    private String deadRoutingKey;

    /**
     * 消息体JSON
     */
    @TableField("messageBody")
    private String messageBody;

    /**
     * 消息头JSON
     */
    private String headers;

    /**
     * 进入死信原因
     */
    @TableField("deadReason")
    private String deadReason;

    /**
     * 最后一次处理错误
     */
    @TableField("lastError")
    private String lastError;

    /**
     * 状态：0待处理 1已重投 2忽略 3重投失败
     * @see MqDeadMessageStatusEnum
     */
    private MqDeadMessageStatusEnum status;

    /**
     * 人工重投次数
     */
    @TableField("retryCount")
    private Integer retryCount;

    /**
     * 最后一次人工重投时间
     */
    @TableField("lastRetryTime")
    private LocalDateTime lastRetryTime;
}
