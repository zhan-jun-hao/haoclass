package com.haoclass.main.application.converter.client;

import com.haoclass.main.infrastructure.persistence.po.CourseLearningProgress;
import com.haoclass.main.interfaces.vo.learningprogress.client.response.ClientCourseLearningProgressVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientCourseLearningProgressConverter {

    ClientCourseLearningProgressConverter INSTANCE = Mappers.getMapper(ClientCourseLearningProgressConverter.class);

    ClientCourseLearningProgressVo poToVo(CourseLearningProgress progress);

    List<ClientCourseLearningProgressVo> poToVo(List<CourseLearningProgress> progressList);
}
