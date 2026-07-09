package com.haoclass.main.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.mq.request.AdminMqDeadMessagePageReqVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageBasicRespVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageDetailRespVo;

public interface AdminDeadMessagesApplicationService {

    PageResult<AdminMqDeadMessageBasicRespVo> getDeadMessagesPageList(AdminMqDeadMessagePageReqVo reqVo);

    AdminMqDeadMessageDetailRespVo getDetail(Long id);

    void ignoreDeadMessage(Long id);

    void retryDeadMessage(Long id);
}
