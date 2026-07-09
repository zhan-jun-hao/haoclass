package com.haoclass.main.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.model.query.MqDeadMessageQuery;
import com.haoclass.main.domain.service.MqDeadMessageService;
import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.MqDeadMessageMapper;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.security.context.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class MqDeadMessageServiceImpl extends ServiceImpl<MqDeadMessageMapper, MqDeadMessage>
        implements MqDeadMessageService {

    @Override
    public IPage<MqDeadMessage> pageList(MqDeadMessageQuery query) {
        if (query == null) {
            query = new MqDeadMessageQuery();
        }
        LambdaQueryWrapper<MqDeadMessage> wrapper = Wrappers.lambdaQuery(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .like(StringUtils.hasText(query.getBizType()), MqDeadMessage::getBizType, query.getBizType())
                .eq(StringUtils.hasText(query.getBizId()), MqDeadMessage::getBizId, query.getBizId())
                .eq(Objects.nonNull(query.getStatus()), MqDeadMessage::getStatus, query.getStatus())
                .like(StringUtils.hasText(query.getSourceQueue()), MqDeadMessage::getSourceQueue, query.getSourceQueue())
                .ge(Objects.nonNull(query.getCreateTimeStart()), MqDeadMessage::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), MqDeadMessage::getCreateTime, query.getCreateTimeEnd())
                .orderByDesc(MqDeadMessage::getCreateTime);

        return this.page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public void updateIgnoreStatus(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<MqDeadMessage> wrapper = Wrappers.lambdaUpdate(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .eq(MqDeadMessage::getId, id)
                .in(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.PENDING_PROCESS, MqDeadMessageStatusEnum.RESEND_FAIL)
                .set(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.IGNORE)
                .set(MqDeadMessage::getUpdateTime, now)
                .set(MqDeadMessage::getUpdatedUser, UserContextHolder.getUserId());

        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.badRequest("忽略死信消息时失败");
        }
    }

    @Override
    public MqDeadMessage findPendingById(Long id) {
        LambdaQueryWrapper<MqDeadMessage> wrapper = Wrappers.lambdaQuery(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .eq(MqDeadMessage::getId, id)
                .in(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.PENDING_PROCESS, MqDeadMessageStatusEnum.RESEND_FAIL);

        return this.getOne(wrapper, false);
    }

    @Override
    public MqDeadMessage findById(Long id) {
        LambdaQueryWrapper<MqDeadMessage> wrapper = Wrappers.lambdaQuery(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .eq(MqDeadMessage::getId, id);

        return this.getOne(wrapper, false);
    }

    @Override
    public void markStatusRetry(Long id) {
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<MqDeadMessage> wrapper = Wrappers.lambdaUpdate(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .eq(MqDeadMessage::getId, id)
                .in(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.PENDING_PROCESS, MqDeadMessageStatusEnum.RESEND_FAIL)
                .set(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.RESEND_SUCCESS)
                .set(MqDeadMessage::getUpdateTime, now)
                .set(MqDeadMessage::getUpdatedUser, UserContextHolder.getUserId())
                .set(MqDeadMessage::getLastRetryTime, now)
                .setIncrBy(MqDeadMessage::getRetryCount, 1);
        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.badRequest("重投死信消息时失败");
        }
    }

    @Override
    public void markFailed(Long id, String error) {
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<MqDeadMessage> wrapper = Wrappers.lambdaUpdate(MqDeadMessage.class)
                .eq(MqDeadMessage::getDeleted, 0)
                .eq(MqDeadMessage::getId, id)
                .in(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.PENDING_PROCESS, MqDeadMessageStatusEnum.RESEND_FAIL)
                .set(MqDeadMessage::getStatus, MqDeadMessageStatusEnum.RESEND_FAIL)
                .set(MqDeadMessage::getLastError, error)
                .set(MqDeadMessage::getLastRetryTime, now)
                .setIncrBy(MqDeadMessage::getRetryCount , 1)
                .set(MqDeadMessage::getUpdateTime, now)
                .set(MqDeadMessage::getUpdatedUser, UserContextHolder.getUserId());
        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.badRequest("重投失败死信消息时失败");
        }
    }
}


