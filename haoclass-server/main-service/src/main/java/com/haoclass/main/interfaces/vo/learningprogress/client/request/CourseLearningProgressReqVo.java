package com.haoclass.main.interfaces.vo.learningprogress.client.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseLearningProgressReqVo {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 章节ID
     */
    @NotNull(message = "章节ID不能为空")
    private Long episodeId;

    /**
     * 播放进度，单位：秒
     */
    @NotNull(message = "播放进度不能为空")
    @Min(value = 0, message = "播放进度不能小于0")
    private Integer progressSeconds;
}