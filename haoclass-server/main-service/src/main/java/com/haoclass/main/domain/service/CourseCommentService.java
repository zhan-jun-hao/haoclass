package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;

public interface CourseCommentService extends IService<CourseComment> {

    /**
     * 分页查询评论
     * @param query
     * @return
     */
    IPage<CourseComment> pageQuery(CourseCommentQuery query);

    /**
     * 通过id查询课程评论
     * @param id
     * @return
     */
    CourseComment getCourseCommentById(Long id);

    /**
     * 新增课程评论
     * @param courseComment
     */
    void saveCourseComment(CourseComment courseComment);

    /**
     * 根据id删除课程评论
     * @param id
     */
    void deleteCourseCommentById(Long id);

    /**
     * 更新评论状态
     * @param id
     * @param status
     */
    void updateStatusById(Long id, CourseCommentStatusEnum status);

    /**
     * 增加点赞数
     * @param id
     */
    void increaseLikeCount(Long id);

    /**
     * 减少点赞数
     * @param id
     */
    void decreaseLikeCount(Long id);

    /**
     * 用户删除评论
     * @param id
     * @param userId
     */
    void deleteUserCourseCommentById(Long id, Long userId);
}
