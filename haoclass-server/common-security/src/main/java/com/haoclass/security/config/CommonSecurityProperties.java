package com.haoclass.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 读取内部密钥
 */
@ConfigurationProperties(prefix = "haoclass.security")
public class CommonSecurityProperties {

    private String internalSecret = "haoclass-internal-request-secret-change-me";

    public String getInternalSecret() {
        return internalSecret;
    }

    public void setInternalSecret(String internalSecret) {
        this.internalSecret = internalSecret;
    }
}
