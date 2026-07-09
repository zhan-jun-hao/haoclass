package com.haoclass.main.application.service.client.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.client.ClientCourseConverter;
import com.haoclass.main.application.service.client.ClientCourseApplicationService;
import com.haoclass.main.domain.service.CourseEpisodeService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseDetailVo;
import com.haoclass.main.interfaces.vo.episode.client.response.ClientCourseEpisodeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCourseApplicationServiceImpl implements ClientCourseApplicationService {

    private final CourseService courseService;

    private final CourseEpisodeService courseEpisodeService;

    @Override
    public PageResult<ClientCourseBasicVo> getCoursePageList(ClientCoursePageQueryReqVo query) {
        IPage<Course> page = courseService.pagePublishedQuery(query);
        List<ClientCourseBasicVo> records = ClientCourseConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public ClientCourseDetailVo getCourseDetail(Long id) {
        Course course = courseService.getPublishedCourseById(id);
        return ClientCourseConverter.INSTANCE.poToDetailVo(course);
    }

    @Override
    public List<ClientCourseEpisodeVo> getCourseEpisodeList(Long courseId) {
        Course course = courseService.getPublishedCourseById(courseId);
        List<CourseEpisode> episodes = courseEpisodeService.listPublishedByCourseId(course.getId());
        return ClientCourseConverter.INSTANCE.episodePoToVo(episodes);
    }
}
