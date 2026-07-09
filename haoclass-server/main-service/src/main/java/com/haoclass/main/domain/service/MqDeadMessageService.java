package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.domain.model.query.MqDeadMessageQuery;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;

public interface MqDeadMessageService extends IService<MqDeadMessage> {

    IPage<MqDeadMessage> pageList(MqDeadMessageQuery query);

    void updateIgnoreStatus(Long id);

    MqDeadMessage findPendingById(Long id);

    MqDeadMessage findById(Long id);

    void markStatusRetry(Long id);

    void markFailed(Long id, String error);
}
