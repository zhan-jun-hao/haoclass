package com.haoclass.common.trace;

import java.util.UUID;

/**
 * 链路追踪ID生成器
 */
public final class TraceIdGenerator {

    private TraceIdGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
