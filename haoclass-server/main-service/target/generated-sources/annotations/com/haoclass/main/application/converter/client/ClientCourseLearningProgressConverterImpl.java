package com.haoclass.main.application.converter.client;

import com.haoclass.main.infrastructure.persistence.po.CourseLearningProgress;
import com.haoclass.main.interfaces.vo.learningprogress.client.response.ClientCourseLearningProgressVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientCourseLearningProgressConverterImpl implements ClientCourseLearningProgressConverter {

    @Override
    public ClientCourseLearningProgressVo poToVo(CourseLearningProgress progress) {
        if ( progress == null ) {
            return null;
        }

        ClientCourseLearningProgressVo clientCourseLearningProgressVo = new ClientCourseLearningProgressVo();

        clientCourseLearningProgressVo.setId( progress.getId() );
        clientCourseLearningProgressVo.setUserId( progress.getUserId() );
        clientCourseLearningProgressVo.setCourseId( progress.getCourseId() );
        clientCourseLearningProgressVo.setEpisodeId( progress.getEpisodeId() );
        clientCourseLearningProgressVo.setProgressSeconds( progress.getProgressSeconds() );
        clientCourseLearningProgressVo.setFinished( progress.getFinished() );
        clientCourseLearningProgressVo.setFinishTime( progress.getFinishTime() );
        clientCourseLearningProgressVo.setLastLearnTime( progress.getLastLearnTime() );
        clientCourseLearningProgressVo.setUpdateTime( progress.getUpdateTime() );

        return clientCourseLearningProgressVo;
    }

    @Override
    public List<ClientCourseLearningProgressVo> poToVo(List<CourseLearningProgress> progressList) {
        if ( progressList == null ) {
            return null;
        }

        List<ClientCourseLearningProgressVo> list = new ArrayList<ClientCourseLearningProgressVo>( progressList.size() );
        for ( CourseLearningProgress courseLearningProgress : progressList ) {
            list.add( poToVo( courseLearningProgress ) );
        }

        return list;
    }
}
