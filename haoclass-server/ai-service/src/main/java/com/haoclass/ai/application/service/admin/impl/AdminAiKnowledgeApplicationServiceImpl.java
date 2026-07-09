package com.haoclass.ai.application.service.admin.impl;

import com.haoclass.ai.application.service.admin.AdminAiKnowledgeApplicationService;
import com.haoclass.ai.infrastructure.support.AiTextSplitter;
import com.haoclass.ai.interfaces.vo.ai.admin.request.AdminAiKnowledgeIngestReqVo;
import com.haoclass.ai.interfaces.vo.ai.admin.response.AdminAiKnowledgeIngestRespVo;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端AI知识库应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminAiKnowledgeApplicationServiceImpl implements AdminAiKnowledgeApplicationService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AiTextSplitter textSplitter;

    @Override
    public AdminAiKnowledgeIngestRespVo ingest(AdminAiKnowledgeIngestReqVo reqVo) {
        // 1.先把input切割成chunk片段
        List<String> chunks = textSplitter.split(reqVo.getContent());
        int count = 0;
        // 2.将chunk存入向量数据库
        for (String chunk : chunks) {
            // 2.1 加工原文
            String segmentText = buildSegmentText(reqVo, chunk);
            // 2.2 将原文统一成langchain4j的格式
            TextSegment segment = TextSegment.from(segmentText);
            // 2.3 将这个原文通过向量模型生成向量
            Response<Embedding> embedding = embeddingModel.embed(segment);
            // 2.4 向量 + 原文 插入到向量数据库中
            embeddingStore.add(embedding.content(), segment);
            count++;
        }

        return new AdminAiKnowledgeIngestRespVo(count);
    }

    /**
     * 第一版先把标题和来源直接拼进片段文本里。
     * 后面做知识库表时，再把这些信息放到metadata中会更规范。
     */
    private String buildSegmentText(AdminAiKnowledgeIngestReqVo reqVo, String chunk) {
        String source = reqVo.getSource() == null || reqVo.getSource().isBlank() ? "人工录入" : reqVo.getSource();
        return "标题：" + reqVo.getTitle() + "\n来源：" + source + "\n内容：" + chunk;
    }
}
