package com.haoclass.main.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminCourseEpisodeApplicationService;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.request.CourseEpisodeUpdateReqVo;
import com.haoclass.main.interfaces.vo.episode.admin.response.AdminCourseEpisodeBasicVo;
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

import java.util.List;

/**
 * 后台管理-admin端-课程章节
 *
 * @author zhanjunhao
 * @since 2026/05/28
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/admin/courses/{courseId}/episodes")
public class AdminCourseEpisodeController {

    private final AdminCourseEpisodeApplicationService adminCourseEpisodeApplicationService;

    /**
     * 查询课程集数
     * @param courseId
     * @return
     */
    @GetMapping
    public Result<List<AdminCourseEpisodeBasicVo>> getEpisodeList(@PathVariable("courseId") Long courseId) {
        log.info("查询课程集数列表, courseId: {}", courseId);
        return Result.success(adminCourseEpisodeApplicationService.getEpisodeList(courseId));
    }

    /**
     * 查看课程集数详情
     * @param courseId
     * @param episodeId
     * @return
     */
    @GetMapping("{episodeId}")
    public Result<AdminCourseEpisodeBasicVo> getEpisodeDetail(@PathVariable("courseId") Long courseId,
                                                              @PathVariable("episodeId") Long episodeId) {
        log.info("查询课程集数详情, courseId: {}, episodeId: {}", courseId, episodeId);
        return Result.success(adminCourseEpisodeApplicationService.getEpisodeDetail(courseId, episodeId));
    }

    /**
     * 添加课程集数
     * @param courseId
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<Void> addEpisode(@PathVariable("courseId") Long courseId,
                                   @RequestBody @Valid CourseEpisodeReqVo reqVo) {
        log.info("新增课程集数, courseId: {}, reqVo: {}", courseId, JSONObject.toJSONString(reqVo));
        adminCourseEpisodeApplicationService.addNewEpisode(courseId, reqVo);
        return Result.success();
    }

    /**
     * 修改课程集数
     * @param courseId
     * @param episodeId
     * @param reqVo
     * @return
     */
    @PutMapping("{episodeId}")
    public Result<Void> updateEpisode(@PathVariable("courseId") Long courseId,
                                      @PathVariable("episodeId") Long episodeId,
                                      @RequestBody @Valid CourseEpisodeUpdateReqVo reqVo) {
        log.info("修改课程集数, courseId: {}, episodeId: {}, reqVo: {}",
                courseId, episodeId, JSONObject.toJSONString(reqVo));
        adminCourseEpisodeApplicationService.modifyEpisode(courseId, episodeId, reqVo);
        return Result.success();
    }

    /**
     * 逻辑删除课程集数
     * @param courseId
     * @param episodeId
     * @return
     */
    @DeleteMapping("{episodeId}")
    public Result<Void> deleteEpisode(@PathVariable("courseId") Long courseId,
                                      @PathVariable("episodeId") Long episodeId) {
        log.info("删除课程集数, courseId: {}, episodeId: {}", courseId, episodeId);
        adminCourseEpisodeApplicationService.removeEpisode(courseId, episodeId);
        return Result.success();
    }
}
