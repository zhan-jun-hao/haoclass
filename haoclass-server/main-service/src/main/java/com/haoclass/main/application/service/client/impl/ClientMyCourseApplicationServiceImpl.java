package com.haoclass.main.application.service.client.impl;

import com.haoclass.main.application.service.client.ClientMyCourseApplicationService;
import com.haoclass.main.infrastructure.persistence.mapper.ClientMyCourseMapper;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientMyCourseApplicationServiceImpl implements ClientMyCourseApplicationService {

    private final ClientMyCourseMapper clientMyCourseMapper;

    @Override
    public List<ClientMyCourseVo> getMyCourseByUserId() {
        return clientMyCourseMapper.selectMyCourses(SecurityUserHolder.getUserId());
    }
}
