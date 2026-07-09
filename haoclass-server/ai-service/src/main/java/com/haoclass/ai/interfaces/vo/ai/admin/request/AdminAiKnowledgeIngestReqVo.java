package com.haoclass.ai.interfaces.vo.ai.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理端知识入库请求对象
 */
@Data
public class AdminAiKnowledgeIngestReqVo {

    /**
     * 知识标题
     */
    @NotBlank(message = "知识标题不能为空")
    private String title;

    /**
     * 知识来源。例如：产品设计书、后台配置、人工录入
     */
    private String source;

    /**
     * 知识正文
     */
    @NotBlank(message = "知识正文不能为空")
    private String content;
}
