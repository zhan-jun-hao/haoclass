package com.haoclass.main.infrastructure.ratelimit;

import cn.hutool.core.util.IdUtil;
import com.haoclass.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载redis限流脚本
 */
@Component
@RequiredArgsConstructor
public class RedisSliderWindowRateLimiter {

    private static final String KEY_PREFIX = "haoclass:rate-limit:";

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> sliderWindowRateLimitScript;

    /**
     *
     * @param key 限流对象key
     * @param limit 允许最大次数
     * @param window 限流窗口时间
     */
    public void check(String key, int limit, long window) {
        List<String> keys = new ArrayList<>();
        keys.add(KEY_PREFIX + key);
        String now = System.currentTimeMillis() + "";
        String requestId = IdUtil.fastUUID();
        Long result = stringRedisTemplate.execute(sliderWindowRateLimitScript,
                keys,
                now,
                window + "",
                limit + "",
                requestId);

        if (result == null || result == 0) {
            throw BusinessException.badRequest(
                    "操作过于频繁，请稍后再试"
            );
        }
    }
}