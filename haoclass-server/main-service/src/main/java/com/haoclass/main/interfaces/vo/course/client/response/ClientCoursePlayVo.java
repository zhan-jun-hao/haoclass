package com.haoclass.main.interfaces.vo.course.client.response;

import lombok.Data;

@Data
public class ClientCoursePlayVo {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节ID
     */
    private Long episodeId;

    /**
     * 章节标题
     */
    private String episodeTitle;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 视频时长，单位：秒
     */
    private Integer durationSeconds;
}