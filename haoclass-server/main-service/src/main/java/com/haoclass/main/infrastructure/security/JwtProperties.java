package com.haoclass.main.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "haoclass.jwt")
public class JwtProperties {

    private String secret = "haoclass-jwt-secret-key-must-be-at-least-32-bytes";

    private Long expireSeconds = 86400L;

    private String issuer = "haoclass";

    private String header = "Authorization";

    private String tokenPrefix = "Bearer ";
}
