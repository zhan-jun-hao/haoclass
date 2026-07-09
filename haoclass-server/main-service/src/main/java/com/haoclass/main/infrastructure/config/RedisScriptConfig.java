package com.haoclass.main.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * lua脚本加载配置类
 */
@Configuration
public class RedisScriptConfig {

    @Bean("sliderWindowRateLimitScript")
    public DefaultRedisScript<Long> sliderWindowRateLimitScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/slider-window-rate-limit.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
