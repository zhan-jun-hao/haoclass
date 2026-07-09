package com.haoclass.main.interfaces.vo.course.admin.request;

import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCourseUpdateReqVo {

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程分类名称
     */
    private String categoryName;

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
     * 排序值，越大越靠前
     */
    private Integer sort;

    /**
     * 课程状态：0草稿 1上架 2下架
     */
    private CourseStatusEnum status;

    /**
     * 收费类型：0免费 1付费 2VIP免费
     */
    private CourseChargeTypeEnum chargeType;
}