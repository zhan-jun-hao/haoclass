package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminCourseConverter {

    AdminCourseConverter INSTANCE = Mappers.getMapper(AdminCourseConverter.class);

    List<AdminCourseBasicVo> poToBasicVo(List<Course> courseList);

    AdminCourseDetailVo poToDetailVo(Course course);
}
