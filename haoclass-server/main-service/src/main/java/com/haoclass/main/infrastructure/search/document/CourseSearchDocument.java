package com.haoclass.main.infrastructure.search.document;

import lombok.Data;

/**
 * 课程搜索文档
 */
@Data
public class CourseSearchDocument {

    /**
     * 课程ID。ES里使用字符串保存，避免前端Long精度问题
     */
    private String id;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程副标题
     */
    private String subtitle;

    /**
     * 课程封面图URL
     */
    private String coverUrl;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 课程摘要
     */
    private String summary;

    /**
     * 课程售价，单位：分
     */
    private Integer price;

    /**
     * 章节数量
     */
    private Integer episodeCount;

    /**
     * 购买人数/销量
     */
    private Integer buyCount;

    /**
     * 排序值，越大越靠前
     */
    private Integer sort;

    /**
     * 课程状态：0草稿 1上架 2下架
     */
    private Integer status;

    /**
     * 收费类型：0免费 1付费 2VIP免费
     */
    private Integer chargeType;

    /**
     * 创建时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTime;
}
