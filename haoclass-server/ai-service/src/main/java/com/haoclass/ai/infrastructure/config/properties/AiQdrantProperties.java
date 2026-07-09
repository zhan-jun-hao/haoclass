package com.haoclass.ai.infrastructure.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Qdrant向量数据库配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "haoclass.ai.qdrant")
public class AiQdrantProperties {

    /**
     * Qdrant主机。
     */
    @NotBlank(message = "Qdrant主机不能为空")
    private String host = "localhost";

    /**
     * Qdrant gRPC端口。LangChain4j读写向量时使用。
     */
    @Min(value = 1, message = "Qdrant gRPC端口必须大于0")
    private Integer port = 6334;

    /**
     * Qdrant HTTP端口。自动创建集合时使用。
     */
    @Min(value = 1, message = "Qdrant HTTP端口必须大于0")
    private Integer httpPort = 6333;

    /**
     * Qdrant集合名称。
     */
    @NotBlank(message = "Qdrant集合名称不能为空")
    private String collectionName = "haoclass_customer_service";

    /**
     * 向量维度。必须和embedding模型输出维度一致。
     */
    @Min(value = 1, message = "Qdrant向量维度必须大于0")
    private Integer vectorSize = 1024;

    /**
     * 启动时是否自动创建Qdrant集合。
     */
    private Boolean autoCreateCollection = true;
}
