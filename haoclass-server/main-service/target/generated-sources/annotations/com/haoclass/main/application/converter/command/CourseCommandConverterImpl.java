package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseUpdateReqVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class CourseCommandConverterImpl implements CourseCommandConverter {

    @Override
    public Course reqVoToPo(AdminCourseReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        Course course = new Course();

        course.setCategoryName( reqVo.getCategoryName() );
        course.setTitle( reqVo.getTitle() );
        course.setSubtitle( reqVo.getSubtitle() );
        course.setCoverUrl( reqVo.getCoverUrl() );
        course.setTeacherName( reqVo.getTeacherName() );
        course.setSummary( reqVo.getSummary() );
        course.setDetail( reqVo.getDetail() );
        course.setPrice( reqVo.getPrice() );
        course.setSort( reqVo.getSort() );
        course.setStatus( reqVo.getStatus() );
        course.setChargeType( reqVo.getChargeType() );

        return course;
    }

    @Override
    public Course updateVoToPo(AdminCourseUpdateReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        Course course = new Course();

        course.setCategoryName( reqVo.getCategoryName() );
        course.setTitle( reqVo.getTitle() );
        course.setSubtitle( reqVo.getSubtitle() );
        course.setCoverUrl( reqVo.getCoverUrl() );
        course.setTeacherName( reqVo.getTeacherName() );
        course.setSummary( reqVo.getSummary() );
        course.setDetail( reqVo.getDetail() );
        course.setPrice( reqVo.getPrice() );
        course.setSort( reqVo.getSort() );
        course.setStatus( reqVo.getStatus() );
        course.setChargeType( reqVo.getChargeType() );

        return course;
    }
}
