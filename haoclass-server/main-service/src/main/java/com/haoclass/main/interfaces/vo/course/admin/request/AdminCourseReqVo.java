package com.haoclass.main.interfaces.vo.course.admin.request;

import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseStatusEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCourseReqVo {

    /**
     * 课程标题
     */
    @NotBlank(message = "课程标题不能为空")
    private String title;

    /**
     * 课程分类名称
     */
    @NotBlank(message = "课程分类名称不能为空")
    private String categoryName;

    /**
     * 课程副标题/卖点文案
     */
    @NotBlank(message = "课程副标题不能为空")
    private String subtitle;

    /**
     * 封面图URL
     */
    @NotBlank(message = "课程封面不能为空")
    private String coverUrl;

    /**
     * 讲师名称
     */
    @NotBlank(message = "讲师名称不能为空")
    private String teacherName;

    /**
     * 课程摘要
     */
    @NotBlank(message = "课程摘要不能为空")
    private String summary;

    /**
     * 课程详情
     */
    @NotBlank(message = "课程详情不能为空")
    private String detail;

    /**
     * 课程售价，单位：分
     */
    @NotNull(message = "课程售价不能为空")
    private Integer price;

    /**
     * 排序值，越大越靠前
     */
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于0")
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