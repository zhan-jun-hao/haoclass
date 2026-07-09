package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseCommentCommandConverter {

    CourseCommentCommandConverter INSTANCE = Mappers.getMapper(CourseCommentCommandConverter.class);

    CourseComment reqVoToPo(ClientCourseCommentReqVo reqVo);
}
