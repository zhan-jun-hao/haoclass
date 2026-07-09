package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.admin.request.AdminCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentBasicVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminCourseCommentConverterImpl implements AdminCourseCommentConverter {

    @Override
    public List<AdminCourseCommentBasicVo> poToBasicVo(List<CourseComment> courseCommentList) {
        if ( courseCommentList == null ) {
            return null;
        }

        List<AdminCourseCommentBasicVo> list = new ArrayList<AdminCourseCommentBasicVo>( courseCommentList.size() );
        for ( CourseComment courseComment : courseCommentList ) {
            list.add( courseCommentToAdminCourseCommentBasicVo( courseComment ) );
        }

        return list;
    }

    @Override
    public AdminCourseCommentDetailVo poToDetailVo(CourseComment courseComment) {
        if ( courseComment == null ) {
            return null;
        }

        AdminCourseCommentDetailVo adminCourseCommentDetailVo = new AdminCourseCommentDetailVo();

        adminCourseCommentDetailVo.setId( courseComment.getId() );
        adminCourseCommentDetailVo.setCourseId( courseComment.getCourseId() );
        adminCourseCommentDetailVo.setEpisodeId( courseComment.getEpisodeId() );
        adminCourseCommentDetailVo.setUserId( courseComment.getUserId() );
        adminCourseCommentDetailVo.setParentId( courseComment.getParentId() );
        adminCourseCommentDetailVo.setRootId( courseComment.getRootId() );
        adminCourseCommentDetailVo.setContent( courseComment.getContent() );
        adminCourseCommentDetailVo.setLikeCount( courseComment.getLikeCount() );
        adminCourseCommentDetailVo.setStatus( courseComment.getStatus() );
        adminCourseCommentDetailVo.setCreatedUser( courseComment.getCreatedUser() );
        adminCourseCommentDetailVo.setUpdatedUser( courseComment.getUpdatedUser() );
        adminCourseCommentDetailVo.setCreateTime( courseComment.getCreateTime() );
        adminCourseCommentDetailVo.setUpdateTime( courseComment.getUpdateTime() );

        return adminCourseCommentDetailVo;
    }

    @Override
    public CourseCommentQuery reqVoToQuery(AdminCourseCommentPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseCommentQuery courseCommentQuery = new CourseCommentQuery();

        courseCommentQuery.setCurrent( reqVo.getCurrent() );
        courseCommentQuery.setSize( reqVo.getSize() );
        courseCommentQuery.setCourseId( reqVo.getCourseId() );
        courseCommentQuery.setEpisodeId( reqVo.getEpisodeId() );
        courseCommentQuery.setUserId( reqVo.getUserId() );
        courseCommentQuery.setRootId( reqVo.getRootId() );
        courseCommentQuery.setStatus( reqVo.getStatus() );
        courseCommentQuery.setContent( reqVo.getContent() );
        courseCommentQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        courseCommentQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );

        return courseCommentQuery;
    }

    protected AdminCourseCommentBasicVo courseCommentToAdminCourseCommentBasicVo(CourseComment courseComment) {
        if ( courseComment == null ) {
            return null;
        }

        AdminCourseCommentBasicVo adminCourseCommentBasicVo = new AdminCourseCommentBasicVo();

        adminCourseCommentBasicVo.setId( courseComment.getId() );
        adminCourseCommentBasicVo.setCourseId( courseComment.getCourseId() );
        adminCourseCommentBasicVo.setEpisodeId( courseComment.getEpisodeId() );
        adminCourseCommentBasicVo.setUserId( courseComment.getUserId() );
        adminCourseCommentBasicVo.setParentId( courseComment.getParentId() );
        adminCourseCommentBasicVo.setRootId( courseComment.getRootId() );
        adminCourseCommentBasicVo.setContent( courseComment.getContent() );
        adminCourseCommentBasicVo.setLikeCount( courseComment.getLikeCount() );
        adminCourseCommentBasicVo.setStatus( courseComment.getStatus() );
        adminCourseCommentBasicVo.setCreateTime( courseComment.getCreateTime() );
        adminCourseCommentBasicVo.setUpdateTime( courseComment.getUpdateTime() );

        return adminCourseCommentBasicVo;
    }
}
