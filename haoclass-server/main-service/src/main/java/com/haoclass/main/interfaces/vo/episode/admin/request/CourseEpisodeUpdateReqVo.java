package com.haoclass.main.interfaces.vo.episode.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEpisodeUpdateReqVo {

    /**
     * 章节标题
     */
    private String title;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 视频时长，单位：秒
     */
    private Integer durationSeconds;

    /**
     * 是否试看：0否 1是
     */
    private Integer freePreview;

    /**
     * 排序值，越大越靠前
     */
    private Integer sort;

    /**
     * 章节状态
     */
    private Integer status;
}