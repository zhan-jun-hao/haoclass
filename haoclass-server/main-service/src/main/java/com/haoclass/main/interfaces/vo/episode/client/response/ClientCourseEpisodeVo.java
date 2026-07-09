package com.haoclass.main.interfaces.vo.episode.client.response;

import lombok.Data;

@Data
public class ClientCourseEpisodeVo {

    /**
     * 章节ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节标题
     */
    private String title;

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
}