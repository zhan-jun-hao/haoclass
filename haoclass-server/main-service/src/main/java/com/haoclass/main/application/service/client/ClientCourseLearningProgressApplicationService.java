package com.haoclass.main.application.service.client;

import com.haoclass.main.interfaces.vo.learningprogress.client.request.CourseLearningProgressReqVo;
import com.haoclass.main.interfaces.vo.learningprogress.client.response.ClientCourseLearningProgressVo;

import java.util.List;

public interface ClientCourseLearningProgressApplicationService {

    /**
     * 用户上报学习进度
     * @param reqVo
     */
    void reportProgress(CourseLearningProgressReqVo reqVo);

    /**
     * 查询用户课程进度
     * @param courseId
     * @return
     */
    List<ClientCourseLearningProgressVo> listCourseProgress(Long courseId);

    /**
     * 查询用户课程章节进度
     * @param courseId
     * @param episodeId
     * @return
     */
    ClientCourseLearningProgressVo getEpisodeProgress(Long courseId, Long episodeId);

}
