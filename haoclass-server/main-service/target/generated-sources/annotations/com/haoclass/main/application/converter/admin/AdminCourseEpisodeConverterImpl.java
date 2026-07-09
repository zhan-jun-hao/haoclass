package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.episode.admin.response.AdminCourseEpisodeBasicVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminCourseEpisodeConverterImpl implements AdminCourseEpisodeConverter {

    @Override
    public List<AdminCourseEpisodeBasicVo> poToBasicVo(List<CourseEpisode> episodes) {
        if ( episodes == null ) {
            return null;
        }

        List<AdminCourseEpisodeBasicVo> list = new ArrayList<AdminCourseEpisodeBasicVo>( episodes.size() );
        for ( CourseEpisode courseEpisode : episodes ) {
            list.add( poToBasicVo( courseEpisode ) );
        }

        return list;
    }

    @Override
    public AdminCourseEpisodeBasicVo poToBasicVo(CourseEpisode episode) {
        if ( episode == null ) {
            return null;
        }

        AdminCourseEpisodeBasicVo adminCourseEpisodeBasicVo = new AdminCourseEpisodeBasicVo();

        adminCourseEpisodeBasicVo.setId( episode.getId() );
        adminCourseEpisodeBasicVo.setCourseId( episode.getCourseId() );
        adminCourseEpisodeBasicVo.setTitle( episode.getTitle() );
        adminCourseEpisodeBasicVo.setVideoUrl( episode.getVideoUrl() );
        adminCourseEpisodeBasicVo.setDurationSeconds( episode.getDurationSeconds() );
        adminCourseEpisodeBasicVo.setFreePreview( episode.getFreePreview() );
        adminCourseEpisodeBasicVo.setSort( episode.getSort() );
        adminCourseEpisodeBasicVo.setStatus( episode.getStatus() );

        return adminCourseEpisodeBasicVo;
    }
}
