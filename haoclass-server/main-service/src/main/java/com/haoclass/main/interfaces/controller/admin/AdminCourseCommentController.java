package com.haoclass.main.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminCourseCommentApplicationService;
import com.haoclass.main.interfaces.vo.comment.admin.request.AdminCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentBasicVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/admin/course-comments")
public class AdminCourseCommentController {

    private final AdminCourseCommentApplicationService adminCourseCommentApplicationService;

    @GetMapping
    public Result<PageResult<AdminCourseCommentBasicVo>> getCourseCommentPageList(
            @Valid AdminCourseCommentPageQueryReqVo query) {
        log.info("后台分页查询课程评论, query: {}", JSONObject.toJSONString(query));
        return Result.success(adminCourseCommentApplicationService.getCourseCommentPageList(query));
    }

    @GetMapping("{id}")
    public Result<AdminCourseCommentDetailVo> getCourseCommentDetail(@PathVariable("id") Long id) {
        log.info("后台查询课程评论详情, id: {}", id);
        return Result.success(adminCourseCommentApplicationService.getCourseCommentDetail(id));
    }

    @DeleteMapping("{id}")
    public Result<Void> deleteCourseComment(@PathVariable("id") Long id) {
        log.info("后台删除课程评论, id: {}", id);
        adminCourseCommentApplicationService.removeCourseComment(id);
        return Result.success();
    }

    @PutMapping("{id}/approve")
    public Result<Void> approveCourseComment(@PathVariable("id") Long id) {
        log.info("后台审核通过课程评论, id: {}", id);
        adminCourseCommentApplicationService.approveCourseComment(id);
        return Result.success();
    }

    @PutMapping("{id}/reject")
    public Result<Void> rejectCourseComment(@PathVariable("id") Long id) {
        log.info("后台审核驳回课程评论, id: {}", id);
        adminCourseCommentApplicationService.rejectCourseComment(id);
        return Result.success();
    }

    @PutMapping("{id}/hide")
    public Result<Void> hideCourseComment(@PathVariable("id") Long id) {
        log.info("后台隐藏课程评论, id: {}", id);
        adminCourseCommentApplicationService.hideCourseComment(id);
        return Result.success();
    }
}
