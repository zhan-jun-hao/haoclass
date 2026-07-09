package com.haoclass.main.domain.model.query;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 课程搜索分页结果
 */
@Data
@Getter
public class CourseSearchPage {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页条数
     */
    private Long size;

    /**
     * 搜索结果
     */
    private List<CourseSearchRecord> records;

}
