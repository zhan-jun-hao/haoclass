package com.haoclass.main.domain.service.context;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import com.haoclass.main.infrastructure.persistence.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户鉴权上下文对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAccessContext {

    /**
     * 用户id
     */
    private User user;

    /**
     * 课程
     */
    private Course course;

    /**
     * 课程章节
     */
    private CourseEpisode courseEpisode;

    /**
     * 用户课程权益
     */
    private CourseUser courseUser;

    /**
     * 能否观看
     */
    private Boolean canWatch;

    private String denyReason;
}
