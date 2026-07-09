package com.haoclass.main.interfaces.controller.client;

import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCoursePlayApplicationService;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCoursePlayVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * c端-课程模块-用户播放课程章节
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/client/play/courses")
public class ClientCoursePlayController {

    private final ClientCoursePlayApplicationService clientCoursePlayApplicationService;

    /**
     * 用户观看鉴权
     * @param courseId
     * @param episodeId
     * @return
     */
    @GetMapping("{courseId}/episodes/{episodeId}")
    public Result<ClientCoursePlayVo> getPlayCourse(@PathVariable("courseId") @NotNull Long courseId,
                                                    @PathVariable("episodeId") @NotNull Long episodeId) {
        log.info("用户播放鉴权接口: /api/main/client/play/courses/{}/episodes/{}", courseId, episodeId);
        return Result.success(clientCoursePlayApplicationService.getPlayCourse(courseId, episodeId));
    }

}
