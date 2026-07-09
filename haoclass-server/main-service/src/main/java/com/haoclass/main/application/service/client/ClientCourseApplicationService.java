package com.haoclass.main.application.service.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseDetailVo;
import com.haoclass.main.interfaces.vo.episode.client.response.ClientCourseEpisodeVo;

import java.util.List;

public interface ClientCourseApplicationService {

    PageResult<ClientCourseBasicVo> getCoursePageList(ClientCoursePageQueryReqVo query);

    ClientCourseDetailVo getCourseDetail(Long id);

    List<ClientCourseEpisodeVo> getCourseEpisodeList(Long courseId);
}
