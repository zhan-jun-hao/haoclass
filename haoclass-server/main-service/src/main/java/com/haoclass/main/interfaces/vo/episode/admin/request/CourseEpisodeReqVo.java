package com.haoclass.main.interfaces.vo.episode.admin.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEpisodeReqVo {

    /**
     * 章节标题
     */
    @NotBlank(message = "章节标题不能为空")
    private String title;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 视频时长，单位：秒
     */
    @NotNull(message = "视频时长不能为空")
    @Min(value = 0, message = "视频时长不能小于0")
    private Integer durationSeconds;

    /**
     * 是否试看：0否 1是
     */
    @NotNull(message = "是否试看不能为空")
    @Min(value = 0, message = "是否试看值不正确")
    @Max(value = 1, message = "是否试看值不正确")
    private Integer freePreview;

    /**
     * 排序值，越大越靠前
     */
    @NotNull(message = "排序值不能为空")
    private Integer sort;

    /**
     * 章节状态
     */
    private Integer status;
}