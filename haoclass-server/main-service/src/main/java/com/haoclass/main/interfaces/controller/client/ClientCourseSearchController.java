package com.haoclass.main.interfaces.controller.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseSearchApplicationService;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCourseSearchReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseSearchRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端课程搜索接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/client/courses/search")
public class ClientCourseSearchController {

    private final ClientCourseSearchApplicationService clientCourseSearchApplicationService;

    /**
     * 搜索已上架课程
     *
     * @param reqVo 搜索请求对象
     * @return 课程搜索分页结果
     */
    @GetMapping
    public Result<PageResult<ClientCourseSearchRespVo>> searchCourses(@Valid ClientCourseSearchReqVo reqVo) {
        return Result.success(clientCourseSearchApplicationService.searchCourses(reqVo));
    }
}
