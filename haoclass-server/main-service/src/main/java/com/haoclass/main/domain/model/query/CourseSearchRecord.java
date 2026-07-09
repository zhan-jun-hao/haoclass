package com.haoclass.main.domain.model.query;

import com.haoclass.main.infrastructure.search.document.CourseSearchDocument;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 课程搜索单条记录
 */
@Data
public class CourseSearchRecord {

    /**
     * ES课程文档
     */
    private CourseSearchDocument document;

    /**
     * 高亮信息，key为字段名，value为高亮片段
     */
    private Map<String, List<String>> highlights;
}