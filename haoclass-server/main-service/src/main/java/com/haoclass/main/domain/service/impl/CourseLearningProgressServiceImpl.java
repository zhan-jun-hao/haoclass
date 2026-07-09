package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.service.CourseLearningProgressService;
import com.haoclass.main.infrastructure.persistence.mapper.CourseLearningProgressMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseLearningProgress;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CourseLearningProgressServiceImpl extends ServiceImpl<CourseLearningProgressMapper, CourseLearningProgress> implements CourseLearningProgressService {

    @Override
    public void upsertProgress(Long userId, Long courseId, Long episodeId, Integer progressSeconds, Integer finished) {
        CourseLearningProgress courseLearningProgress = this.findByUserIdAndEpisodeId(userId, courseId, episodeId);
        LocalDateTime now = LocalDateTime.now();

        if(Objects.isNull(courseLearningProgress)) {

            CourseLearningProgress progress = new CourseLearningProgress();
            progress.setId(IdUtil.getSnowflakeNextId());
            progress.setUserId(userId);
            progress.setCourseId(courseId);
            progress.setEpisodeId(episodeId);
            progress.setProgressSeconds(progressSeconds);
            progress.setFinished(finished);
            progress.setFinishTime(finished == 1 ? now : null);
            progress.setLastLearnTime(now);
            progress.setCreatedUser(userId);
            progress.setUpdatedUser(userId);
            progress.setCreateTime(now);
            progress.setUpdateTime(now);
            progress.setDeleted(0);

            this.save(progress);
        } else {
            LambdaUpdateWrapper<CourseLearningProgress> updateWrapper = Wrappers.lambdaUpdate(CourseLearningProgress.class)
                    .eq(CourseLearningProgress::getId, courseLearningProgress.getId())
                    .eq(CourseLearningProgress::getUserId, userId)
                    .eq(CourseLearningProgress::getCourseId, courseId)
                    .eq(CourseLearningProgress::getEpisodeId, episodeId)
                    .set(CourseLearningProgress::getProgressSeconds, progressSeconds)
                    .set(CourseLearningProgress::getFinished, Objects.equals(courseLearningProgress.getFinished(), 1) ? 1 : finished)
                    .set(CourseLearningProgress::getFinishTime, courseLearningProgress.getFinishTime() != null ?
                            courseLearningProgress.getFinishTime() : finished == 1 ? now : null)
                    .set(CourseLearningProgress::getLastLearnTime, now)
                    .set(CourseLearningProgress::getUpdateTime, now)
                    .set(CourseLearningProgress::getUpdatedUser, userId);
            boolean updated = this.update(updateWrapper);
            if(!updated) {
                throw BusinessException.notFound("用户课程进度更新失败");
            }
        }
    }

    @Override
    public CourseLearningProgress findByUserIdAndEpisodeId(Long userId, Long courseId, Long episodeId) {
        LambdaQueryWrapper<CourseLearningProgress> wrapper = Wrappers.lambdaQuery(CourseLearningProgress.class)
                .eq(CourseLearningProgress::getUserId, userId)
                .eq(CourseLearningProgress::getCourseId, courseId)
                .eq(CourseLearningProgress::getEpisodeId, episodeId)
                .eq(CourseLearningProgress::getDeleted, 0);

        return this.getOne(wrapper);
    }

    @Override
    public List<CourseLearningProgress> listByUserIdAndCourseId(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseLearningProgress> wrapper = Wrappers.lambdaQuery(CourseLearningProgress.class)
                .eq(CourseLearningProgress::getUserId, userId)
                .eq(CourseLearningProgress::getCourseId, courseId)
                .eq(CourseLearningProgress::getDeleted, 0)
                .orderByDesc(CourseLearningProgress::getUpdateTime);

        return this.list(wrapper);
    }

    @Override
    public Long countUserLearningRecord(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseLearningProgress> wrapper = Wrappers.lambdaQuery(CourseLearningProgress.class)
                .eq(CourseLearningProgress::getUserId, userId)
                .eq(CourseLearningProgress::getCourseId, courseId)
                .eq(CourseLearningProgress::getFinished, 1)
                .eq(CourseLearningProgress::getDeleted, 0);
        return this.count(wrapper);
    }

    @Override
    public CourseLearningProgress findLastLearningRecord(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseLearningProgress> wrapper = Wrappers.lambdaQuery(CourseLearningProgress.class)
                .eq(CourseLearningProgress::getUserId, userId)
                .eq(CourseLearningProgress::getCourseId, courseId)
                .eq(CourseLearningProgress::getDeleted, 0)
                .orderByDesc(CourseLearningProgress::getLastLearnTime);
        return this.getOne(wrapper, false);
    }
}
