package com.haoclass.gateway.filter;

import com.haoclass.common.context.UserContext;
import com.haoclass.common.context.UserContextHeaders;
import com.haoclass.gateway.security.GatewayJwtTokenProvider;
import com.haoclass.gateway.security.GatewaySecurityProperties;
import com.haoclass.gateway.security.GatewayUserAuthStateService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final byte[] UNAUTHORIZED_BODY =
            "{\"code\":401,\"msg\":\"Login state expired, please log in again\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);

    private static final byte[] SERVICE_UNAVAILABLE_BODY =
            "{\"code\":503,\"msg\":\"Authentication service unavailable\",\"data\":null}"
                    .getBytes(StandardCharsets.UTF_8);

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/main/admin/auth/login",
            "/api/main/client/auth/**",
            "/api/pay/notify/**",
            "/actuator/health",
            "/error"
    );

    private static final List<String> PUBLIC_GET_PATHS = List.of(
            "/api/main/client/courses",
            "/api/main/client/courses/*",
            "/api/main/client/courses/*/episodes"
    );

    private final GatewayJwtTokenProvider jwtTokenProvider;

    private final GatewaySecurityProperties securityProperties;

    private final GatewayUserAuthStateService userAuthStateService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest.Builder requestBuilder = trustedRequestBuilder(exchange);

        // 登录、C端公开课程等接口不需要解析JWT。先放行白名单，避免无Authorization请求被当成异常请求处理
        if(isPublicRequest(exchange)) {
            return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
        }

        String token = resolveToken(exchange);

        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange);
        }

        try {
            UserContext userContext = jwtTokenProvider.parseUserContext(token);
            return userAuthStateService.isValid(userContext)
                    .flatMap(valid -> {
                        if (!valid) {
                            return unauthorized(exchange);
                        }

                        requestBuilder.headers(headers -> {
                            headers.set(UserContextHeaders.USER_ID, userContext.userId().toString());
                            headers.set(UserContextHeaders.USER_ROLE, userContext.role().toString());
                        });
                        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
                    })
                    .onErrorResume(e -> serviceUnavailable(exchange));
        } catch (JwtException | IllegalArgumentException e) {
            return unauthorized(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private ServerHttpRequest.Builder trustedRequestBuilder(ServerWebExchange exchange) {
        return exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(UserContextHeaders.USER_ID);
                    headers.remove(UserContextHeaders.USER_ROLE);
                    headers.remove(UserContextHeaders.INTERNAL_SECRET);
                    headers.set(UserContextHeaders.INTERNAL_SECRET, securityProperties.getInternalSecret());
                });
    }

    private boolean isPublicRequest(ServerWebExchange exchange) {
        HttpMethod method = exchange.getRequest().getMethod();
        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }

        String path = exchange.getRequest().getURI().getPath();
        if (PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            return true;
        }
        return HttpMethod.GET.equals(method)
                && PUBLIC_GET_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String resolveToken(ServerWebExchange exchange) {
        String tokenPrefix = jwtTokenProvider.getTokenPrefix();
        String header = jwtTokenProvider.getHeader();
        if (!StringUtils.hasText(header) || !StringUtils.hasText(tokenPrefix)) {
            return null;
        }

        String bearerToken = exchange.getRequest().getHeaders().getFirst(header);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        return writeError(exchange, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_BODY);
    }

    private Mono<Void> serviceUnavailable(ServerWebExchange exchange) {
        return writeError(exchange, HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE_BODY);
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, byte[] body) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
