package com.haoclass.main.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.admin.AdminCourseConverter;
import com.haoclass.main.application.converter.command.CourseCommandConverter;
import com.haoclass.main.application.service.admin.AdminCourseApplicationService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseUpdateReqVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCourseApplicationServiceImpl implements AdminCourseApplicationService {

    private final CourseService courseService;

    @Override
    public PageResult<AdminCourseBasicVo> getCoursePageList(AdminCoursePageQueryReqVo query) {
        IPage<Course> page = courseService.pageQuery(query);
        List<AdminCourseBasicVo> records = AdminCourseConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public AdminCourseDetailVo getCourseDetail(Long id) {
        Course course = courseService.getCourseById(id);
        return AdminCourseConverter.INSTANCE.poToDetailVo(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNewCourse(AdminCourseReqVo reqVo) {
        Course course = CourseCommandConverter.INSTANCE.reqVoToPo(reqVo);
        courseService.saveCourse(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCourse(Long id, AdminCourseUpdateReqVo reqVo) {
        Course course = CourseCommandConverter.INSTANCE.updateVoToPo(reqVo);
        courseService.updateCourseById(id, course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCourse(Long id) {
        courseService.deleteCourseById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishCourse(Long id) {
        courseService.updateStatusById(id, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishCourse(Long id) {
        courseService.updateStatusById(id, 2);
    }
}
