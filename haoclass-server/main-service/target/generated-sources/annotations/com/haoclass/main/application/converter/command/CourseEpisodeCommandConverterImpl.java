package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeUpdateReqVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:43+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class CourseEpisodeCommandConverterImpl implements CourseEpisodeCommandConverter {

    @Override
    public CourseEpisode reqVoToPo(CourseEpisodeReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseEpisode courseEpisode = new CourseEpisode();

        courseEpisode.setTitle( reqVo.getTitle() );
        courseEpisode.setVideoUrl( reqVo.getVideoUrl() );
        courseEpisode.setDurationSeconds( reqVo.getDurationSeconds() );
        courseEpisode.setFreePreview( reqVo.getFreePreview() );
        courseEpisode.setSort( reqVo.getSort() );
        courseEpisode.setStatus( reqVo.getStatus() );

        return courseEpisode;
    }

    @Override
    public CourseEpisode updateVoToPo(CourseEpisodeUpdateReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseEpisode courseEpisode = new CourseEpisode();

        courseEpisode.setTitle( reqVo.getTitle() );
        courseEpisode.setVideoUrl( reqVo.getVideoUrl() );
        courseEpisode.setDurationSeconds( reqVo.getDurationSeconds() );
        courseEpisode.setFreePreview( reqVo.getFreePreview() );
        courseEpisode.setSort( reqVo.getSort() );
        courseEpisode.setStatus( reqVo.getStatus() );

        return courseEpisode;
    }
}
