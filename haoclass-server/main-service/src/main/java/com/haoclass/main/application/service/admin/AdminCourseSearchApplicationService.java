package com.haoclass.main.application.service.admin;

/**
 * 管理端课程搜索应用服务。
 */
public interface AdminCourseSearchApplicationService {

    /**
     * 重建已上架课程索引。
     *
     * @return 重建课程数量
     */
    Integer rebuildCourseIndex();

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
}
