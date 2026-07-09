package com.haoclass.main.interfaces.vo.comment.client.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCourseCommentPageQueryReqVo {

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
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 章节ID
     */
    @NotNull(message = "课程章节ID不能为空")
    private Long episodeId;

    /**
     * 根评论ID，0表示查询一级评论
     */
    private Long rootId = 0L;
}