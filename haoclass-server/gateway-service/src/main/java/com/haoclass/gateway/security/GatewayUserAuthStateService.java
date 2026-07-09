package com.haoclass.gateway.security;

import com.haoclass.common.context.UserAuthStateConstants;
import com.haoclass.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GatewayUserAuthStateService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> isValid(UserContext userContext) {
        String key = UserAuthStateConstants.key(userContext.userId());
        return redisTemplate.opsForHash()
                .entries(key)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(state -> isValid(state, userContext))
                .defaultIfEmpty(false);
    }

    private boolean isValid(Map<Object, Object> state, UserContext userContext) {
        Integer status = getIntegerValue(state.get(UserAuthStateConstants.FIELD_STATUS));
        Integer role = getIntegerValue(state.get(UserAuthStateConstants.FIELD_ROLE));
        Long authVersion = getLongValue(state.get(UserAuthStateConstants.FIELD_AUTH_VERSION));

        return Objects.equals(status, UserAuthStateConstants.ENABLED_STATUS)
                && Objects.equals(role, userContext.role())
                && Objects.equals(authVersion, userContext.authVersion());
    }

    private Long getLongValue(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }

    private Integer getIntegerValue(Object value) {
        return value == null ? null : Integer.valueOf(value.toString());
    }
}
