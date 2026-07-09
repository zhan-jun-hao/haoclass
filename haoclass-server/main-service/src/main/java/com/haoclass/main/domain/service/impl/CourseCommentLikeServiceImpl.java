package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.main.domain.service.CourseCommentLikeService;
import com.haoclass.main.infrastructure.common.enums.CourseCommentLikeStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.CourseCommentLikeMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseCommentLike;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseCommentLikeServiceImpl extends ServiceImpl<CourseCommentLikeMapper, CourseCommentLike> implements CourseCommentLikeService {

    @Override
    public boolean activateLike(Long userId, Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        // 1.先更新已经取消点赞的记录
        boolean updated = this.update(Wrappers.lambdaUpdate(CourseCommentLike.class)
                .eq(CourseCommentLike::getUserId, userId)
                .eq(CourseCommentLike::getCommentId, commentId)
                .eq(CourseCommentLike::getStatus, CourseCommentLikeStatusEnum.CANCELLED)
                .eq(CourseCommentLike::getDeleted, 0)
                .set(CourseCommentLike::getStatus, CourseCommentLikeStatusEnum.LIKED)
                .set(CourseCommentLike::getUpdateTime, now)
                .set(CourseCommentLike::getUpdatedUser, userId));

        if (updated) {
            return true;
        } else {
            // 2.如果没有之前取消点赞的记录就新增点赞
            return baseMapper.insertIgnore(IdUtil.getSnowflakeNextId(),
                    commentId,
                    userId,
                    now) > 0;
        }
    }

    @Override
    public boolean cancelLike(Long userId, Long commentId) {
        return this.update(Wrappers.lambdaUpdate(CourseCommentLike.class)
                .eq(CourseCommentLike::getUserId, userId)
                .eq(CourseCommentLike::getCommentId, commentId)
                .eq(CourseCommentLike::getDeleted, 0)
                .eq(CourseCommentLike::getStatus, CourseCommentLikeStatusEnum.LIKED)
                .set(CourseCommentLike::getStatus, CourseCommentLikeStatusEnum.CANCELLED)
                .set(CourseCommentLike::getUpdateTime, LocalDateTime.now())
                .set(CourseCommentLike::getUpdatedUser, userId));
    }

}
