package com.haoclass.pay.domain.service;

import com.haoclass.pay.infrastructure.persistence.po.MqMessage;

import java.util.List;

public interface MqMessageService {

    /**
     * 新增本地消息记录
     *
     * @param bizType
     * @param bizId
     * @param exchangeName
     * @param routingKey
     * @param messageBody
     */
    void savePendingMessage(String bizType, String bizId, String exchangeName,
                            String routingKey, String messageBody);

    /**
     * 批量新增本地消息记录。
     *
     * @param messages 本地消息列表
     */
    void savePendingMessages(List<MqMessage> messages);

    /**
     * 查询未发送的本地消息
     *
     * @return
     */
    List<MqMessage> findPendingMessageList();

    /**
     * 更新这个本地消息已经成功发送
     *
     * @param id
     */
    void markStatusSend(Long id);

    /**
     * 标记发送失败以及重试
     *
     * @param id
     * @param error
     */
    void markFailedAndRetryLater(Long id, String error);
}
