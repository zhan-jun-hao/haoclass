package com.haoclass.main.interfaces.vo.mq.response;

import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMqDeadMessageBasicRespVo {

    /**
     * 死信消息ID
     */
    private Long id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 来源队列
     */
    private String sourceQueue;

    /**
     * 死信原因
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
     * 创建时间
     */
    private LocalDateTime createTime;
}
