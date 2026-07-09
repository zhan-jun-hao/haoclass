package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_learning_progress")
public class CourseLearningProgress extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 课程ID
     */
    @TableField("courseId")
    private Long courseId;

    /**
     * 章节ID
     */
    @TableField("episodeId")
    private Long episodeId;

    /**
     * 最后播放进度，单位：秒
     */
    @TableField("progressSeconds")
    private Integer progressSeconds;

    /**
     * 是否学完：0未学完 1已学完
     */
    private Integer finished;

    /**
     * 完成时间
     */
    @TableField("finishTime")
    private LocalDateTime finishTime;

    /**
     * 最后学习时间
     */
    @TableField("lastLearnTime")
    private LocalDateTime lastLearnTime;
}
