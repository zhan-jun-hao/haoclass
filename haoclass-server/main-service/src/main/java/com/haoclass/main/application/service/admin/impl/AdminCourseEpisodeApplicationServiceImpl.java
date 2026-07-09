package com.haoclass.main.application.service.admin.impl;

import com.haoclass.main.application.converter.admin.AdminCourseEpisodeConverter;
import com.haoclass.main.application.converter.command.CourseEpisodeCommandConverter;
import com.haoclass.main.application.service.admin.AdminCourseEpisodeApplicationService;
import com.haoclass.main.domain.service.CourseEpisodeService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeUpdateReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.response.AdminCourseEpisodeBasicVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCourseEpisodeApplicationServiceImpl implements AdminCourseEpisodeApplicationService {

    private final CourseService courseService;

    private final CourseEpisodeService courseEpisodeService;

    @Override
    public List<AdminCourseEpisodeBasicVo> getEpisodeList(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        List<CourseEpisode> episodes = courseEpisodeService.listByCourseId(course.getId());
        return AdminCourseEpisodeConverter.INSTANCE.poToBasicVo(episodes);
    }

    @Override
    public AdminCourseEpisodeBasicVo getEpisodeDetail(Long courseId, Long episodeId) {
        Course course = courseService.getCourseById(courseId);
        CourseEpisode episode = courseEpisodeService.getEpisodeById(course.getId(), episodeId);
        return AdminCourseEpisodeConverter.INSTANCE.poToBasicVo(episode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNewEpisode(Long courseId, CourseEpisodeReqVo reqVo) {
        Course course = courseService.getCourseById(courseId);
        CourseEpisode episode = CourseEpisodeCommandConverter.INSTANCE.reqVoToPo(reqVo);
        courseEpisodeService.saveEpisode(course.getId(), episode);
        refreshEpisodeCount(course.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyEpisode(Long courseId, Long episodeId, CourseEpisodeUpdateReqVo reqVo) {
        Course course = courseService.getCourseById(courseId);
        CourseEpisode episode = CourseEpisodeCommandConverter.INSTANCE.updateVoToPo(reqVo);
        courseEpisodeService.updateEpisodeById(course.getId(), episodeId, episode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeEpisode(Long courseId, Long episodeId) {
        Course course = courseService.getCourseById(courseId);
        courseEpisodeService.deleteEpisodeById(course.getId(), episodeId);
        refreshEpisodeCount(course.getId());
    }

    private void refreshEpisodeCount(Long courseId) {
        Integer episodeCount = courseEpisodeService.countByCourseId(courseId);
        courseService.updateEpisodeCount(courseId, episodeCount);
    }
}
