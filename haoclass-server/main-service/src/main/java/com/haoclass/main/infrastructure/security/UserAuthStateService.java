package com.haoclass.main.infrastructure.security;

import com.haoclass.common.context.UserAuthStateConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAuthStateService {

    private static final long AUTH_STATE_EXTRA_SECONDS = 24 * 60 * 60 * 1000L;

    private final StringRedisTemplate redisTemplate;

    private final JwtProperties jwtProperties;

    public Long refreshAtLogin(LoginUser loginUser) {
        String key = UserAuthStateConstants.key(loginUser.getId());
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        hashOperations.putIfAbsent(key, UserAuthStateConstants.FIELD_AUTH_VERSION, "1");
        hashOperations.putAll(key, Map.of(
                UserAuthStateConstants.FIELD_STATUS, loginUser.getStatus().toString(),
                UserAuthStateConstants.FIELD_ROLE, loginUser.getRole().toString()
        ));
        refreshExpireTime(key);

        return getLongValue(hashOperations.get(key, UserAuthStateConstants.FIELD_AUTH_VERSION));
    }

    public boolean isValid(LoginUser loginUser, Long authVersion) {
        if (authVersion == null) {
            return false;
        }

        String key = UserAuthStateConstants.key(loginUser.getId());
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Integer status = getIntegerValue(hashOperations.get(key, UserAuthStateConstants.FIELD_STATUS));
        Integer role = getIntegerValue(hashOperations.get(key, UserAuthStateConstants.FIELD_ROLE));
        Long currentVersion = getLongValue(hashOperations.get(key, UserAuthStateConstants.FIELD_AUTH_VERSION));

        return Objects.equals(status, UserAuthStateConstants.ENABLED_STATUS)
                && Objects.equals(role, loginUser.getRole())
                && Objects.equals(currentVersion, authVersion);
    }

    public void invalidate(Long userId) {
        updateAndInvalidate(userId, null, null);
    }

    public void updateAndInvalidate(Long userId, Integer status, Integer role) {
        String key = UserAuthStateConstants.key(userId);
        if (!redisTemplate.hasKey(key)) {
            return;
        }
        // TYPE KEY VALUE
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        if (status != null) {
            hashOperations.put(key, UserAuthStateConstants.FIELD_STATUS, status.toString());
        }
        if (role != null) {
            hashOperations.put(key, UserAuthStateConstants.FIELD_ROLE, role.toString());
        }
        hashOperations.increment(key, UserAuthStateConstants.FIELD_AUTH_VERSION, 1);
        refreshExpireTime(key);
    }

    private void refreshExpireTime(String key) {
        redisTemplate.expire(key, Duration.ofSeconds(jwtProperties.getExpireSeconds() + AUTH_STATE_EXTRA_SECONDS));
    }

    private Long getLongValue(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }

    private Integer getIntegerValue(Object value) {
        return value == null ? null : Integer.valueOf(value.toString());
    }
}
