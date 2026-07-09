package com.haoclass.ai.interfaces.vo.ai.admin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端知识入库响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAiKnowledgeIngestRespVo {

    /**
     * 入库片段数量
     */
    private Integer segmentCount;
}
