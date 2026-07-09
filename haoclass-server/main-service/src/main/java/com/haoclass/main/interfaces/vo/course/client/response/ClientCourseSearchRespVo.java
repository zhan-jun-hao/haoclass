package com.haoclass.main.interfaces.vo.course.client.response;

import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import lombok.Data;

/**
 * C端课程搜索响应对象。
 */
@Data
public class ClientCourseSearchRespVo {

    /**
     * 课程ID。
     */
    private Long id;

    /**
     * 课程分类名称。
     */
    private String categoryName;

    /**
     * 课程标题。
     */
    private String title;

    /**
     * 高亮课程标题。
     */
    private String highlightTitle;

    /**
     * 课程副标题。
     */
    private String subtitle;

    /**
     * 高亮课程副标题。
     */
    private String highlightSubtitle;

    /**
     * 课程封面图URL。
     */
    private String coverUrl;

    /**
     * 讲师名称。
     */
    private String teacherName;

    /**
     * 课程摘要。
     */
    private String summary;

    /**
     * 高亮课程摘要。
     */
    private String highlightSummary;

    /**
     * 课程售价，单位：分。
     */
    private Integer price;

    /**
     * 章节数量。
     */
    private Integer episodeCount;

    /**
     * 购买人数/销量。
     */
    private Integer buyCount;

    /**
     * 收费类型：0免费 1付费 2VIP免费。
     */
    private CourseChargeTypeEnum chargeType;
}
