package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.CourseCommentLikeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_comment_like")
public class CourseCommentLike extends BaseEntity {

    @TableField("commentId")
    private Long commentId;

    @TableField("userId")
    private Long userId;

    private CourseCommentLikeStatusEnum status;
}
