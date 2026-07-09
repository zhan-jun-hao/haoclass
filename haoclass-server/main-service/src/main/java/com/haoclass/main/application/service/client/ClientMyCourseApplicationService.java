package com.haoclass.main.application.service.client;

import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;

import java.util.List;

public interface ClientMyCourseApplicationService {

    /**
     * 查询用户已购买课程
     * @return
     */
    List<ClientMyCourseVo> getMyCourseByUserId();

}
