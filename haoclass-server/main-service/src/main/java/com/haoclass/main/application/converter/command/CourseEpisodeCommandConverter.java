package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeUpdateReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseEpisodeCommandConverter {

    CourseEpisodeCommandConverter INSTANCE = Mappers.getMapper(CourseEpisodeCommandConverter.class);

    CourseEpisode reqVoToPo(CourseEpisodeReqVo reqVo);

    CourseEpisode updateVoToPo(CourseEpisodeUpdateReqVo reqVo);
}
