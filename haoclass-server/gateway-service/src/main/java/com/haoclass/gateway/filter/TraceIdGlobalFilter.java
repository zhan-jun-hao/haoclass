package com.haoclass.gateway.filter;

import com.haoclass.common.trace.TraceConstants;
import com.haoclass.common.trace.TraceIdGenerator;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关入口链路追踪过滤器
 */
@Component
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TraceConstants.TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceIdGenerator.generate();
        }
        String currentTraceId = traceId;

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.set(TraceConstants.TRACE_ID_HEADER, currentTraceId))
                .build();
        ServerWebExchange tracedExchange = exchange.mutate().request(request).build();
        tracedExchange.getResponse().getHeaders().set(TraceConstants.TRACE_ID_HEADER, currentTraceId);

        MDC.put(TraceConstants.TRACE_ID_MDC_KEY, currentTraceId);
        return chain.filter(tracedExchange).doFinally(signalType -> MDC.remove(TraceConstants.TRACE_ID_MDC_KEY));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 9;
    }
}
