package com.haoclass.ai.interfaces.vo.ai.client.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端AI客服回答响应对象
 */
@Data
public class ClientAiChatRespVo {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * AI回答内容
     */
    private String answer;

    /**
     * 引用的知识片段
     */
    private List<ClientAiReferenceVo> references = new ArrayList<>();
}
