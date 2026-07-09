package com.haoclass.main.domain.model.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseCommentQuery {

    private Long current;

    private Long size;

    private Long courseId;

    private Long episodeId;

    private Long userId;

    private Long rootId;

    private Integer status;

    private String content;

    private LocalDateTime createTimeStart;

    private LocalDateTime createTimeEnd;
}
