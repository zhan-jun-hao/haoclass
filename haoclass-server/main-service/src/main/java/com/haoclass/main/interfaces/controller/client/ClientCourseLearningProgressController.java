package com.haoclass.main.interfaces.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseLearningProgressApplicationService;
import com.haoclass.main.interfaces.vo.learningprogress.client.request.CourseLearningProgressReqVo;
import com.haoclass.main.interfaces.vo.learningprogress.client.response.ClientCourseLearningProgressVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户课程观看进度
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/client/learning-progress")
public class ClientCourseLearningProgressController {

    private final ClientCourseLearningProgressApplicationService clientCourseLearningProgressApplicationService;

    /**
     * 用户上报学习进度
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<Void> reportProgress(@RequestBody @Valid CourseLearningProgressReqVo reqVo) {
        log.info("用户上报学习进度, /api/main/client/learning-progress -> reqVo: {}", JSONObject.toJSONString(reqVo));
        clientCourseLearningProgressApplicationService.reportProgress(reqVo);
        return Result.success();
    }

    /**
     * 查询课程学习进度
     * @param courseId
     * @return
     */
    @GetMapping("/courses/{courseId}")
    public Result<List<ClientCourseLearningProgressVo>> listCourseProgress(@PathVariable("courseId") Long courseId) {
        log.info("用户查询课程学习进度, /api/main/client/learning-progress/courses/{} ", courseId);
        return Result.success(clientCourseLearningProgressApplicationService.listCourseProgress(courseId));
    }

    /**
     * 查询课程章节学习进度
     * @param courseId
     * @param episodeId
     * @return
     */
    @GetMapping("/courses/{courseId}/episodes/{episodeId}")
    public Result<ClientCourseLearningProgressVo> getEpisodeProgress(@PathVariable("courseId") Long courseId,
                                                                     @PathVariable("episodeId") Long episodeId) {
        log.info("用户查询章节学习进度, /api/main/client/learning-progress/courses/{}/episodes/{} ", courseId, episodeId);
        return Result.success(clientCourseLearningProgressApplicationService.getEpisodeProgress(courseId, episodeId));
    }
}
