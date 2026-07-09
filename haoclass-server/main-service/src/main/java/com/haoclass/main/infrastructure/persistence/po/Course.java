package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {

    /**
     * 课程分类名称，例如：JavaSE、JavaEE、Redis、MySQL。
     */
    @TableField("categoryName")
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
     * 课程封面图 URL
     */
    @TableField("coverUrl")
    private String coverUrl;

    /**
     * 讲师名称
     */
    @TableField("teacherName")
    private String teacherName;

    /**
     * 课程摘要
     */
    private String summary;

    /**
     * 课程详情，可存富文本 HTML 或 Markdown。
     */
    private String detail;

    /**
     * 课程售价，单位：分
     */
    private Integer price;

    /**
     * 课程章节数，由章节表同步维护
     */
    @TableField("episodeCount")
    private Integer episodeCount;

    /**
     * 购买人数/销量
     */
    @TableField("buyCount")
    private Integer buyCount;

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
    @TableField("chargeType")
    private CourseChargeTypeEnum chargeType;
}
