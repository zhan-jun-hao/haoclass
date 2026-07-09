package com.haoclass.main.interfaces.vo.course.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientMyCourseVo {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 章节数量
     */
    private Integer episodeCount;

    /**
     * 已完成章节数量
     */
    private Integer finishedEpisodeCount;

    /**
     * 学习进度百分比
     */
    private Integer learningPercent;

    /**
     * 最近学习章节ID
     */
    private Long lastEpisodeId;

    /**
     * 最近学习章节标题
     */
    private String lastEpisodeTitle;

    /**
     * 最近学习进度，单位：秒
     */
    private Integer lastProgressSeconds;

    /**
     * 最后学习时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLearnTime;

    /**
     * 权益开通时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime grantTime;

    /**
     * 权益过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}