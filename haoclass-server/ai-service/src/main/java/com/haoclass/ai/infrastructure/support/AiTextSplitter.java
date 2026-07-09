package com.haoclass.ai.infrastructure.support;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单文本切分器。
 */
@Component
public class AiTextSplitter {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_OVERLAP = 80;

    /**
     * 将长文本切成适合向量化的小片段。
     *
     * @param content 原始文本
     * @return 文本片段列表
     */
    public List<String> split(String content) {
        String normalized = content == null ? "" : content.trim();
        List<String> chunks = new ArrayList<>();
        if (normalized.isEmpty()) {
            return chunks;
        }

        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + DEFAULT_CHUNK_SIZE, normalized.length());
            chunks.add(normalized.substring(start, end));

            if (end == normalized.length()) {
                break;
            }
            start = Math.max(end - DEFAULT_OVERLAP, start + 1);
        }
        return chunks;
    }
}
