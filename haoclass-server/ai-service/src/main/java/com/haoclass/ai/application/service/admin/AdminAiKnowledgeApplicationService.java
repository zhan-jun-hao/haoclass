package com.haoclass.ai.application.service.admin;

import com.haoclass.ai.interfaces.vo.ai.admin.request.AdminAiKnowledgeIngestReqVo;
import com.haoclass.ai.interfaces.vo.ai.admin.response.AdminAiKnowledgeIngestRespVo;

/**
 * 管理端AI知识库应用服务。
 */
public interface AdminAiKnowledgeApplicationService {

    /**
     * 将知识内容写入向量库。
     *
     * @param reqVo 知识入库请求对象
     * @return 知识入库结果
     */
    AdminAiKnowledgeIngestRespVo ingest(AdminAiKnowledgeIngestReqVo reqVo);
}
