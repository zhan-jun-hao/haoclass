package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_episode")
public class CourseEpisode extends BaseEntity {

    /**
     * 课程ID
     */
    @TableField("courseId")
    private Long courseId;

    /**
     * 章节标题
     */
    private String title;

    /**
     * 视频地址
     */
    @TableField("videoUrl")
    private String videoUrl;

    /**
     * 视频时长（秒）
     */
    @TableField("durationSeconds")
    private Integer durationSeconds;

    /**
     * 是否试看：0否 1是
     */
    @TableField("freePreview")
    private Integer freePreview;

    /**
     * 排序值，越小越靠前
     */
    private Integer sort;

    /**
     * 状态：0草稿 1上架 2下架
     */
    private Integer status;

}