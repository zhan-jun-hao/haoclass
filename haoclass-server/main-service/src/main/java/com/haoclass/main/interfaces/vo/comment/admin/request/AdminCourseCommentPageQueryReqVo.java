package com.haoclass.main.interfaces.vo.comment.admin.request;

import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCourseCommentPageQueryReqVo {

    /**
     * 当前页码，默认从1开始
     */
    @Min(value = 1, message = "当前页不能小于1")
    private Long current = 1L;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Long size = 10L;

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
     * 根评论ID，0表示一级评论
     */
    private Long rootId;

    /**
     * 评论状态
     * @see CourseCommentStatusEnum
     */
    private Integer status;
    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间查询开始值
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间查询结束值
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;
}