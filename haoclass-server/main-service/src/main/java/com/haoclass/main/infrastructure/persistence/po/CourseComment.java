package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_comment")
public class CourseComment extends BaseEntity {

    @TableField("courseId")
    private Long courseId;

    @TableField("episodeId")
    private Long episodeId;

    @TableField("userId")
    private Long userId;

    @TableField("parentId")
    private Long parentId;

    @TableField("rootId")
    private Long rootId;

    private String content;

    @TableField("likeCount")
    private Integer likeCount;

    private CourseCommentStatusEnum status;
}
