package com.haoclass.main.interfaces.controller.client;


import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientMyCourseApplicationService;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * client-我的课程及其课程进度
 */
@Slf4j
@RestController
@RequestMapping("/api/main/client/my/courses")
@RequiredArgsConstructor
public class ClientMyCoursesController {

    private final ClientMyCourseApplicationService clientMyCourseApplicationService;

    /**
     * 查询用户已购买课程及其课程进度
     * @return
     */
    @GetMapping
    public Result<List<ClientMyCourseVo>> getMyCourses() {
        log.info("用户查询已购课程, id: {}", SecurityUserHolder.getUserId());
        return Result.success(clientMyCourseApplicationService.getMyCourseByUserId());
    }

}

