package com.haoclass.gateway.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "haoclass.security")
public class GatewaySecurityProperties {

    private String internalSecret = "haoclass-internal-request-secret-change-me";
}
