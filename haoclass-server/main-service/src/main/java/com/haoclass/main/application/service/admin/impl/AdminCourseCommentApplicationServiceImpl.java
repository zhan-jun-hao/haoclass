package com.haoclass.main.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.admin.AdminCourseCommentConverter;
import com.haoclass.main.application.service.admin.AdminCourseCommentApplicationService;
import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.domain.service.CourseCommentService;
import com.haoclass.main.infrastructure.common.enums.CourseCommentStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.CourseComment;
import com.haoclass.main.interfaces.vo.comment.admin.request.AdminCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentBasicVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCourseCommentApplicationServiceImpl implements AdminCourseCommentApplicationService {

    private final CourseCommentService courseCommentService;

    @Override
    public PageResult<AdminCourseCommentBasicVo> getCourseCommentPageList(AdminCourseCommentPageQueryReqVo query) {
        CourseCommentQuery courseCommentQuery = AdminCourseCommentConverter.INSTANCE.reqVoToQuery(query);
        IPage<CourseComment> page = courseCommentService.pageQuery(courseCommentQuery);
        return PageResult.success(page, AdminCourseCommentConverter.INSTANCE.poToBasicVo(page.getRecords()));
    }

    @Override
    public AdminCourseCommentDetailVo getCourseCommentDetail(Long id) {
        CourseComment courseComment = courseCommentService.getCourseCommentById(id);
        return AdminCourseCommentConverter.INSTANCE.poToDetailVo(courseComment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCourseComment(Long id) {
        courseCommentService.deleteCourseCommentById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveCourseComment(Long id) {
        courseCommentService.updateStatusById(id, CourseCommentStatusEnum.PUBLISHED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectCourseComment(Long id) {
        courseCommentService.updateStatusById(id, CourseCommentStatusEnum.REJECTED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void hideCourseComment(Long id) {
        courseCommentService.updateStatusById(id, CourseCommentStatusEnum.HIDDEN);
    }
}
