package com.haoclass.main.application.converter.client;

import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseMyCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseMyCommentRespVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface ClientCourseCommentConverter {

    ClientCourseCommentConverter INSTANCE = Mappers.getMapper(ClientCourseCommentConverter.class);

    ClientCourseCommentVo poToVo(CourseComment courseComment);

    CourseCommentQuery reqVoToQuery(ClientCourseCommentPageQueryReqVo reqVo);

    CourseCommentQuery myVoToQuery(ClientCourseMyCommentPageQueryReqVo reqVo);

    ClientCourseMyCommentRespVo poToMyVo(CourseComment courseComment);
}
