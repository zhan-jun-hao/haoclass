package com.haoclass.main.application.service.client.impl;

import com.haoclass.main.application.converter.client.ClientCourseLearningProgressConverter;
import com.haoclass.main.application.service.client.ClientCourseLearningProgressApplicationService;
import com.haoclass.main.application.service.client.ClientCoursePlayApplicationService;
import com.haoclass.main.domain.service.CourseLearningProgressService;
import com.haoclass.main.infrastructure.persistence.po.CourseLearningProgress;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.learningprogress.client.request.CourseLearningProgressReqVo;
import com.haoclass.main.interfaces.vo.learningprogress.client.response.ClientCourseLearningProgressVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCoursePlayVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientCourseLearningProgressApplicationServiceImpl implements ClientCourseLearningProgressApplicationService {

    private final CourseLearningProgressService courseLearningProgressService;
    private final ClientCoursePlayApplicationService clientCoursePlayApplicationService;

    @Override
    public void reportProgress(CourseLearningProgressReqVo reqVo) {
        // 1.先播放鉴权
        Long userId = SecurityUserHolder.getUserId();
        ClientCoursePlayVo playVo = clientCoursePlayApplicationService.getPlayCourse(reqVo.getCourseId(), reqVo.getEpisodeId());
        // 2.校验进度时长
        Integer progressSeconds = normalizeProgressSeconds(reqVo.getProgressSeconds(), playVo.getDurationSeconds());
        // 3.校验视频是否看完
        Integer finished = calculateFinished(progressSeconds, playVo.getDurationSeconds());

        courseLearningProgressService.upsertProgress(
                userId,
                reqVo.getCourseId(),
                reqVo.getEpisodeId(),
                progressSeconds,
                finished
        );
    }

    @Override
    public ClientCourseLearningProgressVo getEpisodeProgress(Long courseId, Long episodeId) {
        Long userId = SecurityUserHolder.getUserId();
        CourseLearningProgress progress = courseLearningProgressService.findByUserIdAndEpisodeId(userId, courseId, episodeId);
        if (Objects.isNull(progress)) {
            return emptyProgress(userId, courseId, episodeId);
        }

        return ClientCourseLearningProgressConverter.INSTANCE.poToVo(progress);
    }

    @Override
    public List<ClientCourseLearningProgressVo> listCourseProgress(Long courseId) {
        Long userId = SecurityUserHolder.getUserId();
        List<CourseLearningProgress> progressList = courseLearningProgressService.listByUserIdAndCourseId(userId, courseId);
        return ClientCourseLearningProgressConverter.INSTANCE.poToVo(progressList);
    }

    private ClientCourseLearningProgressVo emptyProgress(Long userId, Long courseId, Long episodeId) {
        ClientCourseLearningProgressVo vo = new ClientCourseLearningProgressVo();
        vo.setUserId(userId);
        vo.setCourseId(courseId);
        vo.setEpisodeId(episodeId);
        vo.setProgressSeconds(0);
        vo.setFinished(0);
        return vo;
    }

    private Integer normalizeProgressSeconds(Integer progressSeconds, Integer durationSeconds) {
        int safeProgressSeconds = Math.max(progressSeconds, 0);
        if (Objects.nonNull(durationSeconds) && durationSeconds > 0) {
            return Math.min(safeProgressSeconds, durationSeconds);
        }

        return safeProgressSeconds;
    }

    private Integer calculateFinished(Integer progressSeconds, Integer durationSeconds) {
        if (Objects.isNull(durationSeconds) || durationSeconds <= 0) {
            return 0;
        }
        // 实际播放器又可能没那么长
        return durationSeconds - progressSeconds <= 5 ? 1 : 0;
    }
}
