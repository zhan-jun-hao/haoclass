package com.haoclass.pay.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.infrastructure.common.enums.MqMessageStatusEnum;
import com.haoclass.pay.infrastructure.persistence.mapper.MqMessageMapper;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

    @Override
    public void savePendingMessage(String bizType, String bizId, String exchangeName, String routingKey, String messageBody) {
        LocalDateTime now = LocalDateTime.now();
        MqMessage mqMessage = new MqMessage();
        mqMessage.setId(IdUtil.getSnowflakeNextId());
        mqMessage.setBizType(bizType);
        mqMessage.setBizId(bizId);
        mqMessage.setExchangeName(exchangeName);
        mqMessage.setRoutingKey(routingKey);
        mqMessage.setStatus(MqMessageStatusEnum.PENDING_SEND);
        mqMessage.setMessageBody(messageBody);
        mqMessage.setRetryCount(0);
        mqMessage.setNextRetryTime(now);
        mqMessage.setCreateTime(now);
        mqMessage.setUpdateTime(now);
        mqMessage.setCreatedUser(0L);
        mqMessage.setUpdatedUser(0L);
        mqMessage.setDeleted(0);
        boolean saved = this.save(mqMessage);
        if(!saved) {
            throw BusinessException.notFound("新增本地消息失败");
        }
    }

    @Override
    public void savePendingMessages(List<MqMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        boolean saved = this.saveBatch(messages);
        if (!saved) {
            throw BusinessException.badRequest("批量新增本地消息失败");
        }
    }

    @Override
    public List<MqMessage> findPendingMessageList() {
        LambdaQueryWrapper<MqMessage> wrapper = Wrappers.lambdaQuery(MqMessage.class)
                .eq(MqMessage::getDeleted, 0)
                .in(MqMessage::getStatus, MqMessageStatusEnum.PENDING_SEND, MqMessageStatusEnum.FAILED_SEND)
                .le(MqMessage::getNextRetryTime, LocalDateTime.now())
                .orderByAsc(MqMessage::getNextRetryTime)
                .last("limit 100");
        return this.list(wrapper);
    }

    @Override
    public void markStatusSend(Long id) {
        LambdaUpdateWrapper<MqMessage> wrapper = Wrappers.lambdaUpdate(MqMessage.class)
                .eq(MqMessage::getDeleted, 0)
                .eq(MqMessage::getId, id)
                .in(MqMessage::getStatus, MqMessageStatusEnum.PENDING_SEND, MqMessageStatusEnum.FAILED_SEND)
                .set(MqMessage::getStatus, MqMessageStatusEnum.SUCCESS_SEND)
                .set(MqMessage::getUpdateTime, LocalDateTime.now())
                .set(MqMessage::getUpdatedUser, 0L);
        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.badRequest("更新未支付消息为已发送 失败");
        }
    }

    @Override
    public void markFailedAndRetryLater(Long id, String error) {
        LambdaUpdateWrapper<MqMessage> wrapper = Wrappers.lambdaUpdate(MqMessage.class)
                .eq(MqMessage::getDeleted, 0)
                .eq(MqMessage::getId, id)
                .in(MqMessage::getStatus, MqMessageStatusEnum.PENDING_SEND, MqMessageStatusEnum.FAILED_SEND)
                .set(MqMessage::getStatus, MqMessageStatusEnum.FAILED_SEND)
                .set(MqMessage::getLastError, error)
                .setIncrBy(MqMessage::getRetryCount, 1)
                .set(MqMessage::getNextRetryTime, LocalDateTime.now().plusMinutes(10))
                .set(MqMessage::getUpdateTime, LocalDateTime.now())
                .set(MqMessage::getUpdatedUser, 0L);
        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.badRequest("更新未支付消息为发送失败 失败");
        }
    }
}
