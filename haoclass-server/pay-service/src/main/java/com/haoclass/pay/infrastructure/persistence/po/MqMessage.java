package com.haoclass.pay.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.pay.infrastructure.common.enums.MqMessageStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * MQ本地消息表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mq_message")
public class MqMessage extends BaseEntity {

    /**
     * 业务类型
     * 例如：PAYMENT_SUCCESS
     */
    @TableField("bizType")
    private String bizType;

    /**
     * 业务ID
     * 例如：paymentNo
     */
    @TableField("bizId")
    private String bizId;

    /**
     * 交换机名称
     */
    @TableField("exchangeName")
    private String exchangeName;

    /**
     * 路由键
     */
    @TableField("routingKey")
    private String routingKey;

    /**
     * 消息体(JSON)
     */
    @TableField("messageBody")
    private String messageBody;

    /**
     * 状态
     * 0-待发送
     * 1-已发送
     * 2-发送失败
     * @see MqMessageStatusEnum
     */
    @TableField("status")
    private MqMessageStatusEnum status;

    /**
     * 重试次数
     */
    @TableField("retryCount")
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    @TableField("nextRetryTime")
    private LocalDateTime nextRetryTime;

    /**
     * 最后一次错误信息
     */
    @TableField("lastError")
    private String lastError;

}