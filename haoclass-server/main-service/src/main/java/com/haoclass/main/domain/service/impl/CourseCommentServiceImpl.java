package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.domain.service.CourseCommentService;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.CourseCommentMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CourseCommentServiceImpl extends ServiceImpl<CourseCommentMapper, CourseComment>
        implements CourseCommentService {

    @Override
    public IPage<CourseComment> pageQuery(CourseCommentQuery query) {
        if (Objects.isNull(query)) {
            query = new CourseCommentQuery();
        }
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1L);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10L);
        }
        if (query.getSize() > 100) {
            query.setSize(100L);
        }
        if (query.getCreateTimeStart() != null
                && query.getCreateTimeEnd() != null
                && query.getCreateTimeStart().isAfter(query.getCreateTimeEnd())) {
            throw BusinessException.badRequest("开始时间不能晚于结束时间");
        }

        LambdaQueryWrapper<CourseComment> wrapper = Wrappers.lambdaQuery(CourseComment.class)
                .eq(CourseComment::getDeleted, 0)
                .eq(Objects.nonNull(query.getCourseId()), CourseComment::getCourseId, query.getCourseId())
                .eq(Objects.nonNull(query.getEpisodeId()), CourseComment::getEpisodeId, query.getEpisodeId())
                .eq(Objects.nonNull(query.getUserId()), CourseComment::getUserId, query.getUserId())
                .eq(Objects.nonNull(query.getRootId()), CourseComment::getRootId, query.getRootId())
                .eq(Objects.nonNull(query.getStatus()), CourseComment::getStatus, query.getStatus())
                .like(StringUtils.hasText(query.getContent()), CourseComment::getContent, query.getContent())
                .ge(Objects.nonNull(query.getCreateTimeStart()), CourseComment::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), CourseComment::getCreateTime, query.getCreateTimeEnd())
                .orderByDesc(query.getRootId() == null || query.getRootId() == 0, CourseComment::getCreateTime)
                .orderByAsc(query.getRootId() != null && query.getRootId() != 0, CourseComment::getCreateTime);

        return this.page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public CourseComment getCourseCommentById(Long id) {
        CourseComment courseComment = this.getOne(Wrappers.lambdaQuery(CourseComment.class)
                .eq(CourseComment::getId, id)
                .eq(CourseComment::getDeleted, 0));
        if (Objects.isNull(courseComment)) {
            throw BusinessException.notFound("评论不存在");
        }

        return courseComment;
    }

    @Override
    public void saveCourseComment(CourseComment courseComment) {
        LocalDateTime now = LocalDateTime.now();
        courseComment.setId(IdUtil.getSnowflakeNextId());
        courseComment.setParentId(courseComment.getParentId() == null ? 0L : courseComment.getParentId());
        courseComment.setRootId(courseComment.getRootId() == null ? 0L : courseComment.getRootId());
        courseComment.setLikeCount(courseComment.getLikeCount() == null ? 0 : courseComment.getLikeCount());
        if (courseComment.getStatus() == null) {
            courseComment.setStatus(CourseCommentStatusEnum.PENDING_REVIEW);
        }
        courseComment.setCreateTime(now);
        courseComment.setUpdateTime(now);
        courseComment.setCreatedUser(SecurityUserHolder.getUserId());
        courseComment.setUpdatedUser(SecurityUserHolder.getUserId());

        boolean saved = this.save(courseComment);
        if (!saved) {
            throw BusinessException.badRequest("新增评论失败");
        }
    }

    @Override
    public void deleteCourseCommentById(Long id) {
        CourseComment courseComment = getCourseCommentById(id);
        courseComment.setUpdateTime(LocalDateTime.now());
        courseComment.setUpdatedUser(SecurityUserHolder.getUserId());

        boolean removed = this.removeById(courseComment);
        if (!removed) {
            throw BusinessException.badRequest("删除评论失败");
        }
    }

    @Override
    public void updateStatusById(Long id, CourseCommentStatusEnum status) {
        if (status == null) {
            throw BusinessException.badRequest("评论状态不能为空");
        }

        LambdaUpdateWrapper<CourseComment> wrapper = Wrappers.lambdaUpdate(CourseComment.class)
                .eq(CourseComment::getId, id)
                .eq(CourseComment::getDeleted, 0)
                .set(CourseComment::getStatus, status)
                .set(CourseComment::getUpdateTime, LocalDateTime.now())
                .set(CourseComment::getUpdatedUser, SecurityUserHolder.getUserId());

        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.notFound("评论不存在或状态更新失败");
        }
    }

    @Override
    public void increaseLikeCount(Long id) {
        LambdaUpdateWrapper<CourseComment> wrapper = Wrappers.lambdaUpdate(CourseComment.class)
                .eq(CourseComment::getId, id)
                .eq(CourseComment::getDeleted, 0)
                .eq(CourseComment::getStatus, CourseCommentStatusEnum.PUBLISHED)
                .setIncrBy(CourseComment::getLikeCount, 1)
                .set(CourseComment::getUpdateTime, LocalDateTime.now())
                .set(CourseComment::getUpdatedUser, SecurityUserHolder.getUserId());

        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.badRequest("评论不存在或未发布");
        }
    }

    @Override
    public void decreaseLikeCount(Long id) {
        LambdaUpdateWrapper<CourseComment> wrapper = Wrappers.lambdaUpdate(CourseComment.class)
                .eq(CourseComment::getId, id)
                .eq(CourseComment::getDeleted, 0)
                .gt(CourseComment::getLikeCount, 0)
                .setDecrBy(CourseComment::getLikeCount, 1)
                .set(CourseComment::getUpdateTime, LocalDateTime.now())
                .set(CourseComment::getUpdatedUser, SecurityUserHolder.getUserId());

        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.badRequest("取消点赞失败");
        }
    }

    @Override
    public void deleteUserCourseCommentById(Long id, Long userId) {
        LambdaUpdateWrapper<CourseComment> wrapper = Wrappers.lambdaUpdate(CourseComment.class)
                .eq(CourseComment::getId, id)
                .eq(CourseComment::getUserId, userId)
                .eq(CourseComment::getDeleted, 0)
                .set(CourseComment::getDeleted, 1)
                .set(CourseComment::getUpdateTime, LocalDateTime.now())
                .set(CourseComment::getUpdatedUser, userId);

        boolean removed = this.update(wrapper);

        if (!removed) {
            throw BusinessException.notFound("评论不存在或无权删除");
        }
    }

}
