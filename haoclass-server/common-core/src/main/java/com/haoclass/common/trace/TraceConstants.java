package com.haoclass.common.trace;

/**
 * 链路追踪常量
 */
public interface TraceConstants {

    /**
     * HTTP/MQ透传的链路追踪请求头
     */
    String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 日志MDC中的链路追踪键
     */
    String TRACE_ID_MDC_KEY = "traceId";
}
