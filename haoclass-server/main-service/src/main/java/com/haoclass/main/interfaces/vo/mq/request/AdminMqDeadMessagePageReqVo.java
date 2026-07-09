package com.haoclass.main.interfaces.vo.mq.request;

import com.haoclass.common.query.TimeRangePageQuery;
import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminMqDeadMessagePageReqVo extends TimeRangePageQuery {

    /**
     * 业务类型：payment.success、payment.failed、payment.expired
     */
    private String bizType;

    /**
     * 业务ID，例如支付单号 paymentNo
     */
    private String bizId;

    /**
     * 处理状态：0待处理 1已重投 2已忽略 3处理失败
     * @see MqDeadMessageStatusEnum
     */
    private MqDeadMessageStatusEnum status;

    /**
     * 死信来源队列
     */
    private String sourceQueue;

}