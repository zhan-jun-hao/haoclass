package com.haoclass.main.application.service.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCourseSearchReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseSearchRespVo;

/**
 * C端课程搜索应用服务。
 */
public interface ClientCourseSearchApplicationService {

    /**
     * 搜索已上架课程。
     *
     * @param reqVo 搜索请求对象
     * @return 搜索分页结果
     */
    PageResult<ClientCourseSearchRespVo> searchCourses(ClientCourseSearchReqVo reqVo);
}
