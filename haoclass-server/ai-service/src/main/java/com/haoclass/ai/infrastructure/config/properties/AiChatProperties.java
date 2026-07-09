package com.haoclass.ai.infrastructure.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * AI对话模型配置。
 */
@Data
@Validated
@ConfigurationProperties(prefix = "haoclass.ai.chat")
public class AiChatProperties {

    /**
     * 对话模型服务地址。百炼OpenAI兼容模式一般以 /compatible-mode/v1 结尾。
     */
    @NotBlank(message = "对话模型服务地址不能为空")
    private String baseUrl;

    /**
     * 百炼API Key。建议通过 DASHSCOPE_API_KEY 环境变量配置，不要写死在代码里。
     */
    @NotBlank(message = "百炼对话模型API Key不能为空，请配置 DASHSCOPE_API_KEY 或 haoclass.ai.chat.api-key")
    private String apiKey;

    /**
     * 对话模型名称，以百炼控制台展示的模型ID为准。
     */
    @NotBlank(message = "对话模型名称不能为空")
    private String modelName;

    /**
     * 生成温度。越低越稳定，客服场景建议低一点。
     */
    private Double temperature = 0.2;

    /**
     * 请求超时时间，单位：秒。
     */
    @Min(value = 1, message = "对话模型请求超时时间必须大于0")
    private Integer timeoutSeconds = 60;
}
