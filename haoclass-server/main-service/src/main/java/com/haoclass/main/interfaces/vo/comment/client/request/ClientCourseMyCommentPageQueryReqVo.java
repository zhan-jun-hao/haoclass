package com.haoclass.main.interfaces.vo.comment.client.request;

import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ClientCourseMyCommentPageQueryReqVo {

    /**
     * 当前页码，默认从1开始
     */
    @Min(value = 1, message = "当前页不能小于1")
    private Long current = 1L;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能少于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Long size = 10L;

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