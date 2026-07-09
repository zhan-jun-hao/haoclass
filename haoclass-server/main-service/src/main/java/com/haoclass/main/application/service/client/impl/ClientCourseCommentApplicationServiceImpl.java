package com.haoclass.main.application.service.client.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.client.ClientCourseCommentConverter;
import com.haoclass.main.application.converter.command.CourseCommentCommandConverter;
import com.haoclass.main.application.service.client.ClientCourseCommentApplicationService;
import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.domain.service.*;
import com.haoclass.main.domain.service.context.CourseAccessContext;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.ClientCourseCommentMapper;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.infrastructure.ratelimit.RedisSliderWindowRateLimiter;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseMyCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseMyCommentRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientCourseCommentApplicationServiceImpl implements ClientCourseCommentApplicationService {

    private static final int COMMENT_CREATE_MAX_COUNT = 5;
    private static final long COMMENT_CREATE_WINDOW = 1000L;
    private static final int COMMENT_LIKE_USER_MAX_COUNT = 60;
    private static final long COMMENT_LIKE_USER_WINDOW = 1000L;
    private static final int COMMENT_LIKE_TARGET_MAX_COUNT = 3;
    private static final long COMMENT_LIKE_TARGET_WINDOW = 1000L;

    private final CourseCommentService courseCommentService;
    private final CourseAccessService courseAccessService;
    private final CourseCommentLikeService courseCommentLikeService;
    private final ClientCourseCommentMapper clientCourseCommentMapper;
    private final RedisSliderWindowRateLimiter rateLimiter;
    private final CourseService courseService;
    private final CourseEpisodeService courseEpisodeService;

    @Override
    public PageResult<ClientCourseCommentVo> getCourseCommentPageList(ClientCourseCommentPageQueryReqVo reqVo) {
        CourseCommentQuery query = ClientCourseCommentConverter.INSTANCE.reqVoToQuery(reqVo);

        IPage<ClientCourseCommentVo> page = clientCourseCommentMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()),
                query,
                SecurityUserHolder.getUserId()
        );
        return PageResult.success(page, page.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientCourseCommentVo addCourseComment(ClientCourseCommentReqVo reqVo) {
        Long userId = SecurityUserHolder.getUserId();
        rateLimiter.check("comment:create:" + userId, COMMENT_CREATE_MAX_COUNT, COMMENT_CREATE_WINDOW);
        // 1.鉴权
        CourseAccessContext context = courseAccessService.checkCourseAccess(reqVo.getCourseId(), reqVo.getEpisodeId());
        if(!context.getCanWatch()) {
            throw BusinessException.noGrant(context.getDenyReason());
        }
        // 2.添加评论
        CourseComment courseComment = CourseCommentCommandConverter.INSTANCE.reqVoToPo(reqVo);
        courseComment.setUserId(context.getUser().getId());
        courseComment.setStatus(CourseCommentStatusEnum.PENDING_REVIEW);
        Long parentId = reqVo.getParentId();
        if (parentId != null && parentId != 0) {
            CourseComment parentComment = courseCommentService.getCourseCommentById(parentId);
            if (parentComment.getStatus() != CourseCommentStatusEnum.PUBLISHED) {
                throw BusinessException.badRequest("不能回复未发布的评论");
            }
            if (!Objects.equals(parentComment.getCourseId(), courseComment.getCourseId())
                    || !Objects.equals(parentComment.getEpisodeId(), courseComment.getEpisodeId())) {
                throw BusinessException.badRequest("回复评论与当前课程不匹配");
            }

            courseComment.setParentId(parentComment.getId());
            courseComment.setRootId(parentComment.getRootId() == 0L ? parentComment.getId() : parentComment.getRootId());
        }
        if(parentId == null || parentId == 0) {
            courseComment.setParentId(0L);
            courseComment.setRootId(0L);
        }
        courseCommentService.saveCourseComment(courseComment);
        ClientCourseCommentVo commentVo = ClientCourseCommentConverter.INSTANCE.poToVo(courseComment);
        commentVo.setNickname(context.getUser().getNickname());
        commentVo.setAvatarUrl(context.getUser().getAvatarUrl());
        commentVo.setLiked(false);
        commentVo.setReplyCount(0L);
        return commentVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMyCourseComment(Long id) {
        Long userId = SecurityUserHolder.getUserId();
        courseCommentService.deleteUserCourseCommentById(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeCourseComment(Long id) {
        Long userId = SecurityUserHolder.getUserId();
        checkCommentLikeRateLimit(userId, id);
        CourseComment courseComment = courseCommentService.getCourseCommentById(id);
        CourseAccessContext context = courseAccessService.checkCourseAccess(courseComment.getCourseId(), courseComment.getEpisodeId());
        if(!context.getCanWatch()) {
            throw BusinessException.noGrant(context.getDenyReason());
        }

        boolean changed = courseCommentLikeService.activateLike(context.getUser().getId(), id);
        if (changed) {
            courseCommentService.increaseLikeCount(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeCourseComment(Long id) {
        Long userId = SecurityUserHolder.getUserId();
        checkCommentLikeRateLimit(userId, id);
        CourseComment courseComment = courseCommentService.getCourseCommentById(id);

        boolean changed = courseCommentLikeService.cancelLike(userId, courseComment.getId());
        if (changed) {
            courseCommentService.decreaseLikeCount(courseComment.getId());
        }
    }

    private void checkCommentLikeRateLimit(Long userId, Long commentId) {
        rateLimiter.check("comment:like:user:" + userId, COMMENT_LIKE_USER_MAX_COUNT, COMMENT_LIKE_USER_WINDOW);
        rateLimiter.check("comment:like:target:" + userId + ":" + commentId,
                COMMENT_LIKE_TARGET_MAX_COUNT,
                COMMENT_LIKE_TARGET_WINDOW);
    }

    @Override
    public PageResult<ClientCourseMyCommentRespVo> getMyCommentPageList(ClientCourseMyCommentPageQueryReqVo reqVo) {
        CourseCommentQuery query = ClientCourseCommentConverter.INSTANCE.myVoToQuery(reqVo);
        query.setUserId(SecurityUserHolder.getUserId());
        IPage<CourseComment> page = courseCommentService.pageQuery(query);
        if(page.getRecords().isEmpty()) {
            return PageResult.success(page, List.of());
        }
        // 1.获得courseMap
        List<Long> courseIds = page.getRecords().stream().map(CourseComment::getCourseId).toList();
        Map<Long, String> courseMap = courseService.findCourseListById(courseIds)
                .stream()
                .collect(Collectors.toMap(Course::getId, Course::getTitle));
        // 2.获得episodeMap
        List<Long> episodeIds = page.getRecords().stream().map(CourseComment::getEpisodeId).toList();
        Map<Long, String> episodeMap = courseEpisodeService.findCourseEpisodeByIds(episodeIds)
                .stream()
                .collect(Collectors.toMap(CourseEpisode::getId, CourseEpisode::getTitle));

        List<ClientCourseMyCommentRespVo> resultList = new ArrayList<>();
        page.getRecords().forEach(courseComment -> {
            ClientCourseMyCommentRespVo vo = new ClientCourseMyCommentRespVo();
            vo = ClientCourseCommentConverter.INSTANCE.poToMyVo(courseComment);
            vo.setCourseTitle(courseMap.get(vo.getCourseId()));
            vo.setEpisodeTitle(episodeMap.get(vo.getEpisodeId()));
            resultList.add(vo);
        });

        return PageResult.success(page, resultList);
    }
}
