package com.haoclass.security.feign;

import com.haoclass.security.config.CommonSecurityProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * feign远程调用时可以配置 可以传用户信息
 */
public class InternalFeignConfiguration {

    @Bean
    public RequestInterceptor userContextRequestInterceptor(CommonSecurityProperties securityProperties) {
        return new UserContextRequestInterceptor(securityProperties);
    }
}
