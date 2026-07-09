package com.haoclass.main.domain.model.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseOrderQuery {

    private Long current;

    private Long size;

    private String orderNo;

    private Long userId;

    private Long courseId;

    private Integer status;

    private Integer payType;

    private LocalDateTime createTimeStart;

    private LocalDateTime createTimeEnd;
}
