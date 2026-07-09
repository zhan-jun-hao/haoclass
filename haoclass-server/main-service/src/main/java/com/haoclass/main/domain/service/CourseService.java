package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCoursePageQueryReqVo;

import java.util.List;

/**
 * 课程仓储接口。
 */
public interface CourseService extends IService<Course> {

    /**
     * 查询课程
     */
    IPage<Course> pageQuery(AdminCoursePageQueryReqVo pageQuery);

    /**
     * C端分页查询已上架课程
     */
    IPage<Course> pagePublishedQuery(ClientCoursePageQueryReqVo pageQuery);

    /**
     * 根据id查询课程
     * @param id
     * @return
     */
    Course getCourseById(Long id);

    /**
     * 批量查询课程
     * @param ids
     * @return
     */
    List<Course> findCourseListById(List<Long> ids);

    /**
     * C端根据id查询已上架课程
     */
    Course getPublishedCourseById(Long id);

    /**
     * 新增课程
     */
    void saveCourse(Course course);

    /**
     * 修改课程
     */
    void updateCourseById(Long id, Course course);

    /**
     * 删除课程
     */
    void deleteCourseById(Long id);

    /**
     * 更新课程上架状态
     * @param id
     * @param status
     */
    void updateStatusById(Long id, Integer status);

    /**
     * 更新课程的集数
     * @param id
     * @param episodeCount
     */
    void updateEpisodeCount(Long id, Integer episodeCount);

    /**
     * 更新课程购买数量
     * @param id
     */
    void updateBuyCountById(Long id);

    /**
     * 退款后减少课程购买数量
     *
     * @param id 课程ID
     */
    void decreaseBuyCountById(Long id);

}
