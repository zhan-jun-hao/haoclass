package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.CourseLearningProgress;

import java.util.List;

public interface CourseLearningProgressService extends IService<CourseLearningProgress> {

    /**
     * 用户上传进度
     * @param userId
     * @param courseId
     * @param episodeId
     * @param progressSeconds
     * @param finished
     */
    void upsertProgress(Long userId, Long courseId, Long episodeId, Integer progressSeconds, Integer finished);

    /**
     * 查询用户课程章节进度
     * @param userId
     * @param courseId
     * @param episodeId
     * @return
     */
    CourseLearningProgress findByUserIdAndEpisodeId(Long userId, Long courseId, Long episodeId);

    /**
     * 查询用户课程进度
     * @param userId
     * @param courseId
     * @return
     */
    List<CourseLearningProgress> listByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * 统计用户对应课程下的已学完章节进度
     * @param userId
     * @param courseId
     * @return
     */
    Long countUserLearningRecord(Long userId, Long courseId);

    /**
     * 查询最近学习的课程进度
     * @param userId
     * @param courseId
     * @return
     */
    CourseLearningProgress findLastLearningRecord(Long userId, Long courseId);
}
