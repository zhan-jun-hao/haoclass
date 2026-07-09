package com.haoclass.security.trace;

import com.haoclass.common.trace.TraceConstants;
import com.haoclass.common.trace.TraceIdGenerator;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 当前线程的链路追踪上下文
 */
public final class TraceContextHolder {

    private TraceContextHolder() {
    }

    public static String getTraceId() {
        return MDC.get(TraceConstants.TRACE_ID_MDC_KEY);
    }

    public static String getOrCreateTraceId() {
        String traceId = getTraceId();
        if(StringUtils.hasText(traceId)) {
            return traceId;
        }
        traceId = TraceIdGenerator.generate();
        setTraceId(traceId);
        return traceId;
    }

    public static String getOrCreateTraceId(String traceId) {
        if (!StringUtils.hasText(traceId)) {
            return getOrCreateTraceId();
        }
        setTraceId(traceId);
        return traceId;
    }

    public static void setTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            MDC.put(TraceConstants.TRACE_ID_MDC_KEY, traceId);
        } else {
            clear();
        }
    }

    public static void clear() {
        MDC.remove(TraceConstants.TRACE_ID_MDC_KEY);
    }
}
