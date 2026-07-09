package com.haoclass.main.application.service.client.impl;


import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.application.service.client.ClientCoursePlayApplicationService;
import com.haoclass.main.domain.service.CourseAccessService;
import com.haoclass.main.domain.service.context.CourseAccessContext;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCoursePlayVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientCoursePlayApplicationServiceImpl implements ClientCoursePlayApplicationService {

    private final CourseAccessService courseAccessService;

    @Override
    public ClientCoursePlayVo getPlayCourse(Long courseId, Long episodeId) {
        CourseAccessContext context = courseAccessService.checkCourseAccess(courseId, episodeId);
        if(!context.getCanWatch()) {
            throw BusinessException.noGrant(context.getDenyReason());
        }
        return createCoursePlayVo(context.getCourseEpisode());
    }

    private ClientCoursePlayVo createCoursePlayVo(CourseEpisode episode) {
        ClientCoursePlayVo vo = new ClientCoursePlayVo();
        vo.setCourseId(episode.getCourseId());
        vo.setEpisodeId(episode.getId());
        vo.setVideoUrl(episode.getVideoUrl());
        vo.setDurationSeconds(episode.getDurationSeconds());
        vo.setEpisodeTitle(episode.getTitle());

        return vo;
    }

}
