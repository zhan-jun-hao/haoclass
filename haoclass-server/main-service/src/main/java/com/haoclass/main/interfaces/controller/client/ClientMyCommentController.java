package com.haoclass.main.interfaces.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseCommentApplicationService;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseMyCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseMyCommentRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/main/client/my/comment")
public class ClientMyCommentController {

    private final ClientCourseCommentApplicationService clientCourseCommentApplicationService;

    @GetMapping
    public Result<PageResult<ClientCourseMyCommentRespVo>> pageList(@Valid ClientCourseMyCommentPageQueryReqVo reqVo) {
        log.info("分页查询我的评论, {}", JSONObject.toJSONString(reqVo));
        return Result.success(clientCourseCommentApplicationService.getMyCommentPageList(reqVo));
    }

}
