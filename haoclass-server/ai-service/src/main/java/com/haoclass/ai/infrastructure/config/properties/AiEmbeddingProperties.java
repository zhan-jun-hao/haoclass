package com.haoclass.ai.infrastructure.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * AI向量模型配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "haoclass.ai.embedding")
public class AiEmbeddingProperties {

    /**
     * 向量模型服务地址。需要和对话模型一样走百炼OpenAI兼容模式。
     */
    @NotBlank(message = "向量模型服务地址不能为空")
    private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    /**
     * 百炼API Key。通常和对话模型使用同一个 DASHSCOPE_API_KEY。
     */
    @NotBlank(message = "百炼向量模型API Key不能为空，请配置 DASHSCOPE_API_KEY 或 haoclass.ai.embedding.api-key")
    private String apiKey;

    /**
     * 向量模型名称。RAG检索不能直接使用qwen对话模型。
     */
    @NotBlank(message = "向量模型名称不能为空")
    private String modelName = "text-embedding-v4";

    /**
     * 向量维度。text-embedding-v4支持自定义维度，Qdrant集合维度必须和这里一致。
     */
    @Min(value = 1, message = "向量维度必须大于0")
    @Max(value = 2048, message = "向量维度不能大于2048")
    private Integer dimensions = 1024;

    /**
     * 请求超时时间，单位：秒。
     */
    @Min(value = 1, message = "向量模型请求超时时间必须大于0")
    private Integer timeoutSeconds = 60;
}
