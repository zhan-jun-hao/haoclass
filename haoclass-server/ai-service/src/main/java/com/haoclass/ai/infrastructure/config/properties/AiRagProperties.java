package com.haoclass.ai.infrastructure.config.properties;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * RAG检索配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "haoclass.ai.rag")
public class AiRagProperties {

    /**
     * 默认召回片段数量。
     */
    @Min(value = 1, message = "默认召回片段数量必须大于0")
    private Integer defaultTopK = 5;

    /**
     * 最低相似度。过低会把不相关资料塞给模型。
     */
    @DecimalMin(value = "0.0", message = "最低相似度不能小于0")
    private Double minScore = 0.65;
}
