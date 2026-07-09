package com.haoclass.main.application.service.admin;

import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeUpdateReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.response.AdminCourseEpisodeBasicVo;

import java.util.List;

public interface AdminCourseEpisodeApplicationService {

    List<AdminCourseEpisodeBasicVo> getEpisodeList(Long courseId);

    AdminCourseEpisodeBasicVo getEpisodeDetail(Long courseId, Long episodeId);

    void addNewEpisode(Long courseId, CourseEpisodeReqVo reqVo);

    void modifyEpisode(Long courseId, Long episodeId, CourseEpisodeUpdateReqVo reqVo);

    void removeEpisode(Long courseId, Long episodeId);
}
