package com.haoclass.main.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.admin.AdminDeadMessageConverter;
import com.haoclass.main.application.service.admin.AdminDeadMessagesApplicationService;
import com.haoclass.main.domain.model.query.MqDeadMessageQuery;
import com.haoclass.main.domain.service.MqDeadMessageService;
import com.haoclass.main.infrastructure.mq.DeadMessageRetryPublisher;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.main.interfaces.vo.mq.request.AdminMqDeadMessagePageReqVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageBasicRespVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageDetailRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminDeadMessagesApplicationServiceImpl implements AdminDeadMessagesApplicationService {

    private final MqDeadMessageService mqDeadMessageService;
    private final DeadMessageRetryPublisher deadMessageRetryPublisher;

    @Override
    public PageResult<AdminMqDeadMessageBasicRespVo> getDeadMessagesPageList(AdminMqDeadMessagePageReqVo reqVo) {
        MqDeadMessageQuery query = AdminDeadMessageConverter.INSTANCE.reqVoToQuery(reqVo);
        IPage<MqDeadMessage> ipage = mqDeadMessageService.pageList(query);
        return PageResult.success(ipage, AdminDeadMessageConverter.INSTANCE.poToRespVo(ipage.getRecords()));
    }

    @Override
    public AdminMqDeadMessageDetailRespVo getDetail(Long id) {
        MqDeadMessage mqDeadMessage = mqDeadMessageService.findById(id);
        if(Objects.isNull(mqDeadMessage)) {
            throw BusinessException.notFound("该死信消息不存在");
        }
        return AdminDeadMessageConverter.INSTANCE.poToDetailVo(mqDeadMessage);
    }

    @Override
    public void ignoreDeadMessage(Long id) {
        mqDeadMessageService.updateIgnoreStatus(id);
    }

    @Override
    public void retryDeadMessage(Long id) {
        // 1.查询待发送消息
        MqDeadMessage pendingMsg = mqDeadMessageService.findPendingById(id);
        if(Objects.isNull(pendingMsg)) {
            throw BusinessException.notFound("该待发送消息已经忽略或者已经重投, 无法发送");
        }
        // 2.发送
        deadMessageRetryPublisher.send(pendingMsg);
    }
}


