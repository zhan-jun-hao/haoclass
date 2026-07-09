package com.haoclass.main.interfaces.vo.comment.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClientCourseMyCommentRespVo {

    /**
     * 评论id
     */
    private Long id;

    /**
     * 课程id 可以跳转课程
     */
    private Long courseId;

    /**
     * 课程章节id
     */
    private Long episodeId;

    /**
     * 课程标题
     */
    private String courseTitle;

    /**
     * 课程章节标题
     */
    private String episodeTitle;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论状态
     * @see CourseCommentStatusEnum
     */
    private CourseCommentStatusEnum status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}