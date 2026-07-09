package com.haoclass.main.domain.service;

import com.haoclass.main.domain.service.context.CourseAccessContext;

/**
 * 用户播放课程鉴权服务
 */
public interface CourseAccessService {

    CourseAccessContext checkCourseAccess(Long courseId, Long episodeId);
}
