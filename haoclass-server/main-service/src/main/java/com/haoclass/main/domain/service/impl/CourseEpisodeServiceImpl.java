package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.service.CourseEpisodeService;
import com.haoclass.main.infrastructure.persistence.mapper.CourseEpisodeMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CourseEpisodeServiceImpl extends ServiceImpl<CourseEpisodeMapper, CourseEpisode>
        implements CourseEpisodeService {

    @Override
    public List<CourseEpisode> listByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseEpisode> wrapper = Wrappers.lambdaQuery(CourseEpisode.class)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getDeleted, 0)
                .orderByAsc(CourseEpisode::getSort)
                .orderByAsc(CourseEpisode::getCreateTime);

        return this.list(wrapper);
    }

    @Override
    public List<CourseEpisode> listPublishedByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseEpisode> wrapper = Wrappers.lambdaQuery(CourseEpisode.class)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getDeleted, 0)
                .eq(CourseEpisode::getStatus, 1)
                .orderByAsc(CourseEpisode::getSort)
                .orderByAsc(CourseEpisode::getCreateTime);

        return this.list(wrapper);
    }

    @Override
    public CourseEpisode getEpisodeById(Long courseId, Long episodeId) {
        LambdaQueryWrapper<CourseEpisode> wrapper = Wrappers.lambdaQuery(CourseEpisode.class)
                .eq(CourseEpisode::getId, episodeId)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getDeleted, 0);

        CourseEpisode episode = this.getOne(wrapper);
        if (Objects.isNull(episode)) {
            throw BusinessException.notFound("集数不存在");
        }

        return episode;
    }

    @Override
    public CourseEpisode getPublishedEpisodeById(Long courseId, Long episodeId) {
        LambdaQueryWrapper<CourseEpisode> wrapper = Wrappers.lambdaQuery(CourseEpisode.class)
                .eq(CourseEpisode::getId, episodeId)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getStatus, 1)
                .eq(CourseEpisode::getDeleted, 0);

        CourseEpisode episode = this.getOne(wrapper);
        if (Objects.isNull(episode)) {
            throw BusinessException.notFound("集数不存在");
        }

        return episode;
    }

    @Override
    public void saveEpisode(Long courseId, CourseEpisode episode) {
        episode.setId(IdUtil.getSnowflakeNextId());
        episode.setCourseId(courseId);
        if (Objects.isNull(episode.getStatus())) {
            episode.setStatus(0);
        }
        episode.setCreateTime(LocalDateTime.now());
        episode.setUpdateTime(LocalDateTime.now());
        episode.setCreatedUser(SecurityUserHolder.getUserId());
        episode.setUpdatedUser(SecurityUserHolder.getUserId());

        this.save(episode);
    }

    @Override
    public void updateEpisodeById(Long courseId, Long episodeId, CourseEpisode episode) {
        CourseEpisode originEpisode = this.getEpisodeById(courseId, episodeId);
        episode.setId(originEpisode.getId());
        episode.setCourseId(courseId);
        episode.setUpdateTime(LocalDateTime.now());
        episode.setUpdatedUser(SecurityUserHolder.getUserId());

        this.updateById(episode);
    }

    @Override
    public void deleteEpisodeById(Long courseId, Long episodeId) {
        LambdaUpdateWrapper<CourseEpisode> wrapper = Wrappers.lambdaUpdate(CourseEpisode.class)
                .eq(CourseEpisode::getId, episodeId)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getDeleted, 0)
                .set(CourseEpisode::getDeleted, 1)
                .set(CourseEpisode::getUpdateTime, LocalDateTime.now())
                .set(CourseEpisode::getUpdatedUser, SecurityUserHolder.getUserId());
        boolean updated = this.update(wrapper);

        if(!updated) {
            throw BusinessException.notFound("课程集数不存在");
        }
    }

    @Override
    public Integer countByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseEpisode> wrapper =  Wrappers.lambdaQuery(CourseEpisode.class)
                .eq(CourseEpisode::getCourseId, courseId)
                .eq(CourseEpisode::getDeleted, 0);

        long count = this.count(wrapper);

        return (int) count;
    }

    @Override
    public List<CourseEpisode> findCourseEpisodeByIds(List<Long> ids) {
        return this.listByIds(ids);
    }
}
