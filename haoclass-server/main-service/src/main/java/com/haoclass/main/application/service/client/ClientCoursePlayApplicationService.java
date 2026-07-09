package com.haoclass.main.application.service.client;

import com.haoclass.main.interfaces.vo.course.client.response.ClientCoursePlayVo;

public interface ClientCoursePlayApplicationService {

    /**
     * 用户播放鉴权
     * @param courseId
     * @param episodeId
     * @return
     */
    ClientCoursePlayVo getPlayCourse(Long courseId, Long episodeId);

}
