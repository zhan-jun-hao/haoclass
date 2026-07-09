package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.episode.admin.response.AdminCourseEpisodeBasicVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminCourseEpisodeConverter {

    AdminCourseEpisodeConverter INSTANCE = Mappers.getMapper(AdminCourseEpisodeConverter.class);

    List<AdminCourseEpisodeBasicVo> poToBasicVo(List<CourseEpisode> episodes);

    AdminCourseEpisodeBasicVo poToBasicVo(CourseEpisode episode);
}
