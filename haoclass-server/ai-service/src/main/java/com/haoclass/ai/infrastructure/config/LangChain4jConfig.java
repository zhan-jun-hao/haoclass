package com.haoclass.ai.infrastructure.config;

import com.haoclass.ai.infrastructure.config.properties.AiChatProperties;
import com.haoclass.ai.infrastructure.config.properties.AiEmbeddingProperties;
import com.haoclass.ai.infrastructure.config.properties.AiQdrantProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * LangChain4j基础配置。
 */
@Configuration
@RequiredArgsConstructor
public class LangChain4jConfig {

    private final AiChatProperties chatProperties;
    private final AiEmbeddingProperties embeddingProperties;
    private final AiQdrantProperties qdrantProperties;

    /**
     * 对话模型。百炼平台通过OpenAI兼容协议接入。
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .apiKey(chatProperties.getApiKey())
                .modelName(chatProperties.getModelName())
                .temperature(chatProperties.getTemperature())
                .timeout(Duration.ofSeconds(chatProperties.getTimeoutSeconds()));

        if (StringUtils.hasText(chatProperties.getBaseUrl())) {
            builder.baseUrl(chatProperties.getBaseUrl());
        }

        return builder.build();
    }

    /**
     * 向量模型。知识入库和用户问题检索必须使用同一种向量模型。
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder = OpenAiEmbeddingModel.builder()
                .apiKey(embeddingProperties.getApiKey())
                .modelName(embeddingProperties.getModelName())
                .dimensions(embeddingProperties.getDimensions())
                .timeout(Duration.ofSeconds(embeddingProperties.getTimeoutSeconds()));

        if (StringUtils.hasText(embeddingProperties.getBaseUrl())) {
            builder.baseUrl(embeddingProperties.getBaseUrl());
        }

        return builder.build();
    }

    /**
     * 向量库。Qdrant负责存储知识片段的向量索引。
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore.builder()
                .host(qdrantProperties.getHost())
                .port(qdrantProperties.getPort())
                .collectionName(qdrantProperties.getCollectionName())
                .build();
    }
}
