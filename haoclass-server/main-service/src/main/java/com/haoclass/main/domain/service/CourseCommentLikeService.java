package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.CourseCommentLike;

public interface CourseCommentLikeService extends IService<CourseCommentLike> {

    /**
     * 激活点赞
     */
    boolean activateLike(Long userId, Long commentId);

    /**
     * 取消点赞
     */
    boolean cancelLike(Long userId, Long commentId);
}
