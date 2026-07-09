package com.haoclass.ai.application.service.client.impl;

import cn.hutool.core.util.IdUtil;
import com.haoclass.ai.application.service.client.ClientAiChatApplicationService;
import com.haoclass.ai.infrastructure.config.properties.AiRagProperties;
import com.haoclass.ai.interfaces.vo.ai.client.request.ClientAiChatReqVo;
import com.haoclass.ai.interfaces.vo.ai.client.response.ClientAiChatRespVo;
import com.haoclass.ai.interfaces.vo.ai.client.response.ClientAiReferenceVo;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 客户端AI客服应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class ClientAiChatApplicationServiceImpl implements ClientAiChatApplicationService {

    private final ChatLanguageModel chatLanguageModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AiRagProperties ragProperties;

    @Override
    public ClientAiChatRespVo chat(ClientAiChatReqVo reqVo) {
        // 1. 将用户问题转换成向量。
        Response<Embedding> questionEmbedding = embeddingModel.embed(reqVo.getQuestion());

        // 2. 用问题向量到Qdrant里检索相似知识片段。
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding.content())
                .maxResults(resolveTopK(reqVo.getTopK()))
                .minScore(ragProperties.getMinScore())
                .build();
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // 3. 把检索结果拼进提示词，再交给大模型生成答案。
        ClientAiChatRespVo respVo = new ClientAiChatRespVo();
        respVo.setConversationId(resolveConversationId(reqVo.getConversationId()));
        respVo.setReferences(toReferences(searchResult.matches()));
        respVo.setAnswer(chatLanguageModel.generate(buildPrompt(reqVo.getQuestion(), searchResult.matches())));
        return respVo;
    }

    private Integer resolveTopK(Integer topK) {
        if (topK == null || topK < 1) {
            return ragProperties.getDefaultTopK();
        }
        return Math.min(topK, 10);
    }

    private String resolveConversationId(String conversationId) {
        if (StringUtils.hasText(conversationId)) {
            return conversationId;
        }
        return String.valueOf(IdUtil.getSnowflakeNextId());
    }

    private List<ClientAiReferenceVo> toReferences(List<EmbeddingMatch<TextSegment>> matches) {
        return matches.stream().map(match -> {
            ClientAiReferenceVo referenceVo = new ClientAiReferenceVo();
            referenceVo.setScore(match.score());
            referenceVo.setText(match.embedded().text());
            return referenceVo;
        }).toList();
    }

    /**
     * RAG提示词：把检索到的资料填给模型，并要求它只基于资料回答。
     */
    private String buildPrompt(String question, List<EmbeddingMatch<TextSegment>> matches) {
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            context.append("资料").append(i + 1).append("：\n")
                    .append(matches.get(i).embedded().text())
                    .append("\n\n");
        }

        return """
                你是好课学堂的AI客服，只回答和好课学堂相关的问题。
                请严格依据【知识库资料】回答用户问题。
                如果知识库资料不足以回答，请明确告诉用户“我暂时没有查到相关资料”，不要编造。
                回答要简洁、礼貌、可执行。

                【知识库资料】
                %s

                【用户问题】
                %s
                """.formatted(context, question);
    }
}
