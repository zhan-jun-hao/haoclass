package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminCourseConverterImpl implements AdminCourseConverter {

    @Override
    public List<AdminCourseBasicVo> poToBasicVo(List<Course> courseList) {
        if ( courseList == null ) {
            return null;
        }

        List<AdminCourseBasicVo> list = new ArrayList<AdminCourseBasicVo>( courseList.size() );
        for ( Course course : courseList ) {
            list.add( courseToAdminCourseBasicVo( course ) );
        }

        return list;
    }

    @Override
    public AdminCourseDetailVo poToDetailVo(Course course) {
        if ( course == null ) {
            return null;
        }

        AdminCourseDetailVo adminCourseDetailVo = new AdminCourseDetailVo();

        adminCourseDetailVo.setId( course.getId() );
        adminCourseDetailVo.setCategoryName( course.getCategoryName() );
        adminCourseDetailVo.setTitle( course.getTitle() );
        adminCourseDetailVo.setSubtitle( course.getSubtitle() );
        adminCourseDetailVo.setCoverUrl( course.getCoverUrl() );
        adminCourseDetailVo.setTeacherName( course.getTeacherName() );
        adminCourseDetailVo.setSummary( course.getSummary() );
        adminCourseDetailVo.setDetail( course.getDetail() );
        adminCourseDetailVo.setPrice( course.getPrice() );
        adminCourseDetailVo.setEpisodeCount( course.getEpisodeCount() );
        adminCourseDetailVo.setBuyCount( course.getBuyCount() );
        adminCourseDetailVo.setSort( course.getSort() );
        adminCourseDetailVo.setStatus( course.getStatus() );
        adminCourseDetailVo.setChargeType( course.getChargeType() );

        return adminCourseDetailVo;
    }

    protected AdminCourseBasicVo courseToAdminCourseBasicVo(Course course) {
        if ( course == null ) {
            return null;
        }

        AdminCourseBasicVo adminCourseBasicVo = new AdminCourseBasicVo();

        adminCourseBasicVo.setId( course.getId() );
        adminCourseBasicVo.setCategoryName( course.getCategoryName() );
        adminCourseBasicVo.setTitle( course.getTitle() );
        adminCourseBasicVo.setSubtitle( course.getSubtitle() );
        adminCourseBasicVo.setCoverUrl( course.getCoverUrl() );
        adminCourseBasicVo.setTeacherName( course.getTeacherName() );
        adminCourseBasicVo.setSummary( course.getSummary() );
        adminCourseBasicVo.setPrice( course.getPrice() );
        adminCourseBasicVo.setEpisodeCount( course.getEpisodeCount() );
        adminCourseBasicVo.setBuyCount( course.getBuyCount() );
        adminCourseBasicVo.setStatus( course.getStatus() );
        adminCourseBasicVo.setChargeType( course.getChargeType() );

        return adminCourseBasicVo;
    }
}
