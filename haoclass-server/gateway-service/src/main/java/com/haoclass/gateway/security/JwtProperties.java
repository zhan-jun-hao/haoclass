package com.haoclass.gateway.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件的jwt配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "haoclass.jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret = "haoclass-jwt-secret-key-must-be-at-least-32-bytes";

    /**
     * 签发者
     */
    private String issuer = "haoclass";

    /**
     * 请求头
     */
    private String header = "Authorization";

    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer ";
}
