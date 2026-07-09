package com.haoclass.ai.interfaces.vo.ai.client.response;

import lombok.Data;

/**
 * AI回答引用片段。
 */
@Data
public class ClientAiReferenceVo {

    /**
     * 相似度分数。
     */
    private Double score;

    /**
     * 知识片段内容。
     */
    private String text;
}
