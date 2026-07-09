package com.haoclass.main.interfaces.vo.course.client.response;

import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import lombok.Data;

@Data
public class ClientCourseDetailVo {

    /**
     * 课程ID
     */
    private Long id;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程副标题/卖点文案
     */
    private String subtitle;

    /**
     * 封面图URL
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
     * 课程详情
     */
    private String detail;

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
     * 收费类型：0免费 1付费 2VIP免费
     */
    private CourseChargeTypeEnum chargeType;
}