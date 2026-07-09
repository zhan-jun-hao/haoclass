package com.haoclass.main.interfaces.controller.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseApplicationService;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseDetailVo;
import com.haoclass.main.interfaces.vo.episode.client.response.ClientCourseEpisodeVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端课程浏览接口。
 */
@RestController
@RequestMapping("/api/main/client/courses")
@RequiredArgsConstructor
public class ClientCourseController {

    private final ClientCourseApplicationService clientCourseApplicationService;

    /**
     * 分页查询课程信息
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<ClientCourseBasicVo>> getCoursePageList(@Valid ClientCoursePageQueryReqVo query) {
        return Result.success(clientCourseApplicationService.getCoursePageList(query));
    }

    /**
     * 查看课程详情
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<ClientCourseDetailVo> getCourseDetail(@PathVariable("id") Long id) {
        return Result.success(clientCourseApplicationService.getCourseDetail(id));
    }

    /**
     * 查询课程下的章节信息
     * @param courseId
     * @return
     */
    @GetMapping("{courseId}/episodes")
    public Result<List<ClientCourseEpisodeVo>> getCourseEpisodeList(@PathVariable("courseId") Long courseId) {
        return Result.success(clientCourseApplicationService.getCourseEpisodeList(courseId));
    }

}
