package com.haoclass.security.filter;

import com.haoclass.common.trace.TraceConstants;
import com.haoclass.security.trace.TraceContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP入口链路追踪过滤器
 */
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = TraceContextHolder.getOrCreateTraceId(request.getHeader(TraceConstants.TRACE_ID_HEADER));
        response.setHeader(TraceConstants.TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContextHolder.clear();
        }
    }
}
