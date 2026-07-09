package com.haoclass.main.interfaces.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseCommentApplicationService;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * c端-课程领域-点赞模块
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/client/course-comments")
public class ClientCourseCommentController {

    private final ClientCourseCommentApplicationService clientCourseCommentApplicationService;

    /**
     * 分页查询评论
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<ClientCourseCommentVo>> getCourseCommentPageList(
            @Valid ClientCourseCommentPageQueryReqVo query) {
        log.info("C端分页查询课程评论, query: {}", JSONObject.toJSONString(query));
        return Result.success(clientCourseCommentApplicationService.getCourseCommentPageList(query));
    }

    /**
     * 用户发表评论
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<ClientCourseCommentVo> addCourseComment(@RequestBody @Valid ClientCourseCommentReqVo reqVo) {
        log.info("C端发表评论, reqVo: {}", JSONObject.toJSONString(reqVo));
        return Result.success(clientCourseCommentApplicationService.addCourseComment(reqVo));
    }

    /**
     * 用户删除评论
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteMyCourseComment(@PathVariable("id") Long id) {
        log.info("C端删除本人课程评论, id: {}", id);
        clientCourseCommentApplicationService.removeMyCourseComment(id);
        return Result.success();
    }

    /**
     * 用户点赞评论
     * @param id
     * @return
     */
    @PutMapping("{id}/like")
    public Result<Void> likeCourseComment(@PathVariable("id") Long id) {
        log.info("C端点赞课程评论, id: {}", id);
        clientCourseCommentApplicationService.likeCourseComment(id);
        return Result.success();
    }

    /**
     * 用户删除评论
     * @param id
     * @return
     */
    @DeleteMapping("{id}/like")
    public Result<Void> unlikeCourseComment(@PathVariable("id") Long id) {
        log.info("C端取消点赞课程评论, id: {}", id);
        clientCourseCommentApplicationService.unlikeCourseComment(id);
        return Result.success();
    }
}
