package com.haoclass.main.interfaces.vo.comment.admin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCourseCommentBasicVo {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节ID
     */
    private Long episodeId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 直接父评论ID，0表示一级评论
     */
    private Long parentId;

    /**
     * 根评论ID，0表示一级评论
     */
    private Long rootId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 评论状态
     */
    private CourseCommentStatusEnum status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}