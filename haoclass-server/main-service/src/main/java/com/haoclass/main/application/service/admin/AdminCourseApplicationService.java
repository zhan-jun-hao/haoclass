package com.haoclass.main.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseUpdateReqVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseDetailVo;

public interface AdminCourseApplicationService {

    /**
     * 根据title、categoryName、status分页查询
     * @param query
     * @return
     */
    PageResult<AdminCourseBasicVo> getCoursePageList(AdminCoursePageQueryReqVo query);

    AdminCourseDetailVo getCourseDetail(Long id);

    /**
     * 添加新课程
     * @param reqVo
     */
    void addNewCourse(AdminCourseReqVo reqVo);

    /**
     * 修改课程
     * @param id
     * @param reqVo
     */
    void modifyCourse(Long id, AdminCourseUpdateReqVo reqVo);

    /**
     * 删除课程
     * @param id
     */
    void removeCourse(Long id);

    /**
     * 上架课程
     * @param id
     */
    void publishCourse(Long id);

    /**
     * 下架课程
     * @param id
     */
    void unpublishCourse(Long id);
}
