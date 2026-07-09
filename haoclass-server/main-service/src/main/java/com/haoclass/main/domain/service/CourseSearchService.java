package com.haoclass.main.domain.service;

import com.haoclass.main.domain.model.query.CourseSearchPage;
import com.haoclass.main.domain.model.query.CourseSearchQuery;

/**
 * 课程搜索服务。
 */
public interface CourseSearchService {

    /**
     * 重建已上架课程索引。
     *
     * @return 重建课程数量
     */
    Integer rebuildPublishedCourseIndex();

    /**
     * 同步单个课程索引。
     *
     * @param courseId 课程ID
     */
    void syncCourseIndex(Long courseId);

    /**
     * 删除单个课程索引。
     *
     * @param courseId 课程ID
     */
    void deleteCourseIndex(Long courseId);

    /**
     * 搜索已上架课程。
     *
     * @param query 搜索参数
     * @return 搜索分页结果
     */
    CourseSearchPage searchPublishedCourses(CourseSearchQuery query);
}
