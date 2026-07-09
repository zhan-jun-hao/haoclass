package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseUpdateReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseCommandConverter {

    CourseCommandConverter INSTANCE = Mappers.getMapper(CourseCommandConverter.class);

    Course reqVoToPo(AdminCourseReqVo reqVo);

    Course updateVoToPo(AdminCourseUpdateReqVo reqVo);
}
