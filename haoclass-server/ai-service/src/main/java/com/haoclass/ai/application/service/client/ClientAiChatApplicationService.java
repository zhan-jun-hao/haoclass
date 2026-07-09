package com.haoclass.ai.application.service.client;

import com.haoclass.ai.interfaces.vo.ai.client.request.ClientAiChatReqVo;
import com.haoclass.ai.interfaces.vo.ai.client.response.ClientAiChatRespVo;

/**
 * 客户端AI客服应用服务。
 */
public interface ClientAiChatApplicationService {

    /**
     * 向AI客服提问。
     *
     * @param reqVo AI客服提问请求对象
     * @return AI客服回答
     */
    ClientAiChatRespVo chat(ClientAiChatReqVo reqVo);
}
