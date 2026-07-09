package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentReqVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class CourseCommentCommandConverterImpl implements CourseCommentCommandConverter {

    @Override
    public CourseComment reqVoToPo(ClientCourseCommentReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseComment courseComment = new CourseComment();

        courseComment.setCourseId( reqVo.getCourseId() );
        courseComment.setEpisodeId( reqVo.getEpisodeId() );
        courseComment.setParentId( reqVo.getParentId() );
        courseComment.setContent( reqVo.getContent() );

        return courseComment;
    }
}
