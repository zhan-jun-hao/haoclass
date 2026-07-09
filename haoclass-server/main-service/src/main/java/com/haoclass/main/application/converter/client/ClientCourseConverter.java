package com.haoclass.main.application.converter.client;

import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseDetailVo;
import com.haoclass.main.interfaces.vo.episode.client.response.ClientCourseEpisodeVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientCourseConverter {

    ClientCourseConverter INSTANCE = Mappers.getMapper(ClientCourseConverter.class);

    List<ClientCourseBasicVo> poToBasicVo(List<Course> courseList);

    ClientCourseDetailVo poToDetailVo(Course course);

    List<ClientCourseEpisodeVo> episodePoToVo(List<CourseEpisode> episodeList);

    ClientMyCourseVo poToMyVo(Course course);
}
