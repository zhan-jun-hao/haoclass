package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.admin.request.AdminCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentBasicVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminCourseCommentConverter {

    AdminCourseCommentConverter INSTANCE = Mappers.getMapper(AdminCourseCommentConverter.class);

    List<AdminCourseCommentBasicVo> poToBasicVo(List<CourseComment> courseCommentList);

    AdminCourseCommentDetailVo poToDetailVo(CourseComment courseComment);

    CourseCommentQuery reqVoToQuery(AdminCourseCommentPageQueryReqVo reqVo);
}
