package com.haoclass.ai.interfaces.vo.ai.client.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 客户端AI客服提问请求对象
 */
@Data
public class ClientAiChatReqVo {

    /**
     * 会话ID 前端首次提问可以不传，后端会生成一个新的
     */
    private String conversationId;

    /**
     * 用户问题
     */
    @NotBlank(message = "问题不能为空")
    private String question;

    /**
     * 本次提问 希望从向量数据库取出最相似的前k条记录
     */
    private Integer topK;
}
