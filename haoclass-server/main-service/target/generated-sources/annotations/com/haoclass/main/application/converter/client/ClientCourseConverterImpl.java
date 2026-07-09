package com.haoclass.main.application.converter.client;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseDetailVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;
import com.haoclass.main.interfaces.vo.episode.client.response.ClientCourseEpisodeVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientCourseConverterImpl implements ClientCourseConverter {

    @Override
    public List<ClientCourseBasicVo> poToBasicVo(List<Course> courseList) {
        if ( courseList == null ) {
            return null;
        }

        List<ClientCourseBasicVo> list = new ArrayList<ClientCourseBasicVo>( courseList.size() );
        for ( Course course : courseList ) {
            list.add( courseToClientCourseBasicVo( course ) );
        }

        return list;
    }

    @Override
    public ClientCourseDetailVo poToDetailVo(Course course) {
        if ( course == null ) {
            return null;
        }

        ClientCourseDetailVo clientCourseDetailVo = new ClientCourseDetailVo();

        clientCourseDetailVo.setId( course.getId() );
        clientCourseDetailVo.setCategoryName( course.getCategoryName() );
        clientCourseDetailVo.setTitle( course.getTitle() );
        clientCourseDetailVo.setSubtitle( course.getSubtitle() );
        clientCourseDetailVo.setCoverUrl( course.getCoverUrl() );
        clientCourseDetailVo.setTeacherName( course.getTeacherName() );
        clientCourseDetailVo.setSummary( course.getSummary() );
        clientCourseDetailVo.setDetail( course.getDetail() );
        clientCourseDetailVo.setPrice( course.getPrice() );
        clientCourseDetailVo.setEpisodeCount( course.getEpisodeCount() );
        clientCourseDetailVo.setBuyCount( course.getBuyCount() );
        clientCourseDetailVo.setChargeType( course.getChargeType() );

        return clientCourseDetailVo;
    }

    @Override
    public List<ClientCourseEpisodeVo> episodePoToVo(List<CourseEpisode> episodeList) {
        if ( episodeList == null ) {
            return null;
        }

        List<ClientCourseEpisodeVo> list = new ArrayList<ClientCourseEpisodeVo>( episodeList.size() );
        for ( CourseEpisode courseEpisode : episodeList ) {
            list.add( courseEpisodeToClientCourseEpisodeVo( courseEpisode ) );
        }

        return list;
    }

    @Override
    public ClientMyCourseVo poToMyVo(Course course) {
        if ( course == null ) {
            return null;
        }

        ClientMyCourseVo clientMyCourseVo = new ClientMyCourseVo();

        clientMyCourseVo.setTitle( course.getTitle() );
        clientMyCourseVo.setCoverUrl( course.getCoverUrl() );
        clientMyCourseVo.setTeacherName( course.getTeacherName() );
        clientMyCourseVo.setEpisodeCount( course.getEpisodeCount() );

        return clientMyCourseVo;
    }

    protected ClientCourseBasicVo courseToClientCourseBasicVo(Course course) {
        if ( course == null ) {
            return null;
        }

        ClientCourseBasicVo clientCourseBasicVo = new ClientCourseBasicVo();

        clientCourseBasicVo.setId( course.getId() );
        clientCourseBasicVo.setCategoryName( course.getCategoryName() );
        clientCourseBasicVo.setTitle( course.getTitle() );
        clientCourseBasicVo.setSubtitle( course.getSubtitle() );
        clientCourseBasicVo.setCoverUrl( course.getCoverUrl() );
        clientCourseBasicVo.setTeacherName( course.getTeacherName() );
        clientCourseBasicVo.setSummary( course.getSummary() );
        clientCourseBasicVo.setPrice( course.getPrice() );
        clientCourseBasicVo.setEpisodeCount( course.getEpisodeCount() );
        clientCourseBasicVo.setBuyCount( course.getBuyCount() );
        clientCourseBasicVo.setChargeType( course.getChargeType() );

        return clientCourseBasicVo;
    }

    protected ClientCourseEpisodeVo courseEpisodeToClientCourseEpisodeVo(CourseEpisode courseEpisode) {
        if ( courseEpisode == null ) {
            return null;
        }

        ClientCourseEpisodeVo clientCourseEpisodeVo = new ClientCourseEpisodeVo();

        clientCourseEpisodeVo.setId( courseEpisode.getId() );
        clientCourseEpisodeVo.setCourseId( courseEpisode.getCourseId() );
        clientCourseEpisodeVo.setTitle( courseEpisode.getTitle() );
        clientCourseEpisodeVo.setDurationSeconds( courseEpisode.getDurationSeconds() );
        clientCourseEpisodeVo.setFreePreview( courseEpisode.getFreePreview() );
        clientCourseEpisodeVo.setSort( courseEpisode.getSort() );

        return clientCourseEpisodeVo;
    }
}
