package com.haoclass.ai.interfaces.controller.admin;

import com.haoclass.ai.application.service.admin.AdminAiKnowledgeApplicationService;
import com.haoclass.ai.interfaces.vo.ai.admin.request.AdminAiKnowledgeIngestReqVo;
import com.haoclass.ai.interfaces.vo.ai.admin.response.AdminAiKnowledgeIngestRespVo;
import com.haoclass.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端AI知识库接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/admin/knowledge")
public class AdminAiKnowledgeController {

    private final AdminAiKnowledgeApplicationService adminAiKnowledgeApplicationService;

    /**
     * 知识入库
     *
     * @param reqVo 知识入库请求对象
     * @return 知识入库结果
     */
    @PostMapping("/ingest")
    public Result<AdminAiKnowledgeIngestRespVo> ingest(@Valid @RequestBody AdminAiKnowledgeIngestReqVo reqVo) {
        System.out.println("DASHSCOPE_API_KEY=" + System.getenv("DASHSCOPE_API_KEY"));
        return Result.success(adminAiKnowledgeApplicationService.ingest(reqVo));
    }
}
