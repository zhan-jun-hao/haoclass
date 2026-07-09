package com.haoclass.main.application.service.admin.impl;

import com.haoclass.main.application.service.admin.AdminCourseSearchApplicationService;
import com.haoclass.main.domain.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 管理端课程搜索应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminCourseSearchApplicationServiceImpl implements AdminCourseSearchApplicationService {

    private final CourseSearchService courseSearchService;

    @Override
    public Integer rebuildCourseIndex() {
        return courseSearchService.rebuildPublishedCourseIndex();
    }

    @Override
    public void syncCourseIndex(Long courseId) {
        courseSearchService.syncCourseIndex(courseId);
    }

    @Override
    public void deleteCourseIndex(Long courseId) {
        courseSearchService.deleteCourseIndex(courseId);
    }
}
