package com.haoclass.main.application.converter.client;

import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseMyCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseMyCommentRespVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientCourseCommentConverterImpl implements ClientCourseCommentConverter {

    @Override
    public ClientCourseCommentVo poToVo(CourseComment courseComment) {
        if ( courseComment == null ) {
            return null;
        }

        ClientCourseCommentVo clientCourseCommentVo = new ClientCourseCommentVo();

        clientCourseCommentVo.setId( courseComment.getId() );
        clientCourseCommentVo.setCourseId( courseComment.getCourseId() );
        clientCourseCommentVo.setEpisodeId( courseComment.getEpisodeId() );
        clientCourseCommentVo.setUserId( courseComment.getUserId() );
        clientCourseCommentVo.setParentId( courseComment.getParentId() );
        clientCourseCommentVo.setRootId( courseComment.getRootId() );
        clientCourseCommentVo.setContent( courseComment.getContent() );
        clientCourseCommentVo.setLikeCount( courseComment.getLikeCount() );
        clientCourseCommentVo.setStatus( courseComment.getStatus() );
        clientCourseCommentVo.setCreateTime( courseComment.getCreateTime() );

        return clientCourseCommentVo;
    }

    @Override
    public CourseCommentQuery reqVoToQuery(ClientCourseCommentPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseCommentQuery courseCommentQuery = new CourseCommentQuery();

        courseCommentQuery.setCurrent( reqVo.getCurrent() );
        courseCommentQuery.setSize( reqVo.getSize() );
        courseCommentQuery.setCourseId( reqVo.getCourseId() );
        courseCommentQuery.setEpisodeId( reqVo.getEpisodeId() );
        courseCommentQuery.setRootId( reqVo.getRootId() );

        return courseCommentQuery;
    }

    @Override
    public CourseCommentQuery myVoToQuery(ClientCourseMyCommentPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseCommentQuery courseCommentQuery = new CourseCommentQuery();

        courseCommentQuery.setCurrent( reqVo.getCurrent() );
        courseCommentQuery.setSize( reqVo.getSize() );
        courseCommentQuery.setStatus( reqVo.getStatus() );
        courseCommentQuery.setContent( reqVo.getContent() );
        courseCommentQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        courseCommentQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );

        return courseCommentQuery;
    }

    @Override
    public ClientCourseMyCommentRespVo poToMyVo(CourseComment courseComment) {
        if ( courseComment == null ) {
            return null;
        }

        ClientCourseMyCommentRespVo clientCourseMyCommentRespVo = new ClientCourseMyCommentRespVo();

        clientCourseMyCommentRespVo.setId( courseComment.getId() );
        clientCourseMyCommentRespVo.setCourseId( courseComment.getCourseId() );
        clientCourseMyCommentRespVo.setEpisodeId( courseComment.getEpisodeId() );
        clientCourseMyCommentRespVo.setContent( courseComment.getContent() );
        clientCourseMyCommentRespVo.setLikeCount( courseComment.getLikeCount() );
        clientCourseMyCommentRespVo.setStatus( courseComment.getStatus() );
        clientCourseMyCommentRespVo.setCreateTime( courseComment.getCreateTime() );

        return clientCourseMyCommentRespVo;
    }
}
