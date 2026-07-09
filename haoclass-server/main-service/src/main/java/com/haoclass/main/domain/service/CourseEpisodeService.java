package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;

import java.util.List;

public interface CourseEpisodeService extends IService<CourseEpisode> {

    /**
     * 获取视频集数
     * @param courseId
     * @return
     */
    List<CourseEpisode> listByCourseId(Long courseId);

    /**
     * 获取发表的课程章节
     * @param courseId
     * @return
     */
    List<CourseEpisode> listPublishedByCourseId(Long courseId);

    /**
     * 获取对应课程对应集
     * @param courseId
     * @param episodeId
     * @return
     */
    CourseEpisode getEpisodeById(Long courseId, Long episodeId);

    /**
     * 获取对应课程已上架对应集
     * @param courseId
     * @param episodeId
     * @return
     */
    CourseEpisode getPublishedEpisodeById(Long courseId, Long episodeId);

    void saveEpisode(Long courseId, CourseEpisode episode);

    void updateEpisodeById(Long courseId, Long episodeId, CourseEpisode episode);

    void deleteEpisodeById(Long courseId, Long episodeId);

    Integer countByCourseId(Long courseId);

    List<CourseEpisode> findCourseEpisodeByIds(List<Long> ids);
}
