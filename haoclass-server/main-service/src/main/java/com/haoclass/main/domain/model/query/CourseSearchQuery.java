package com.haoclass.main.domain.model.query;

import lombok.Data;

/**
 * 课程搜索领域查询对象
 */
@Data
public class CourseSearchQuery {

    /**
     * 当前页码，默认从1开始
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 收费类型：0免费 1付费 2VIP免费
     */
    private Integer chargeType;

    /**
     * 排序类型：0综合 1最新 2销量 3价格升序 4价格降序
     */
    private Integer sortType = 0;
}
