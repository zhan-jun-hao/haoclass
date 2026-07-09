package com.haoclass.main.application.service.client.impl;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.service.client.ClientCourseSearchApplicationService;
import com.haoclass.main.domain.model.query.CourseSearchPage;
import com.haoclass.main.domain.model.query.CourseSearchQuery;
import com.haoclass.main.domain.model.query.CourseSearchRecord;
import com.haoclass.main.domain.service.CourseSearchService;
import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.search.document.CourseSearchDocument;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCourseSearchReqVo;
import com.haoclass.main.interfaces.vo.course.client.response.ClientCourseSearchRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * C端课程搜索应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class ClientCourseSearchApplicationServiceImpl implements ClientCourseSearchApplicationService {

    private final CourseSearchService courseSearchService;

    @Override
    public PageResult<ClientCourseSearchRespVo> searchCourses(ClientCourseSearchReqVo reqVo) {
        CourseSearchPage searchPage = courseSearchService.searchPublishedCourses(toQuery(reqVo));
        List<ClientCourseSearchRespVo> records = searchPage.getRecords().stream()
                .map(this::toRespVo)
                .toList();

        PageResult<ClientCourseSearchRespVo> result = new PageResult<>();
        result.setTotal(searchPage.getTotal());
        result.setCurPage(searchPage.getCurrent());
        result.setSize(searchPage.getSize());
        result.setPages(calcPages(searchPage.getTotal(), searchPage.getSize()));
        result.setRecords(records);
        return result;
    }

    private CourseSearchQuery toQuery(ClientCourseSearchReqVo reqVo) {
        ClientCourseSearchReqVo safeReqVo = reqVo == null ? new ClientCourseSearchReqVo() : reqVo;
        CourseSearchQuery query = new CourseSearchQuery();
        query.setCurrent(safeReqVo.getCurrent());
        query.setSize(safeReqVo.getSize());
        query.setKeyword(safeReqVo.getKeyword());
        query.setCategoryName(safeReqVo.getCategoryName());
        query.setChargeType(safeReqVo.getChargeType());
        query.setSortType(safeReqVo.getSortType());
        return query;
    }

    private ClientCourseSearchRespVo toRespVo(CourseSearchRecord record) {
        CourseSearchDocument document = record.getDocument();
        Map<String, List<String>> highlights = record.getHighlights() == null
                ? Collections.emptyMap()
                : record.getHighlights();

        ClientCourseSearchRespVo respVo = new ClientCourseSearchRespVo();
        respVo.setId(Long.valueOf(document.getId()));
        respVo.setCategoryName(document.getCategoryName());
        respVo.setTitle(document.getTitle());
        respVo.setHighlightTitle(firstHighlight(highlights, "title"));
        respVo.setSubtitle(document.getSubtitle());
        respVo.setHighlightSubtitle(firstHighlight(highlights, "subtitle"));
        respVo.setCoverUrl(document.getCoverUrl());
        respVo.setTeacherName(document.getTeacherName());
        respVo.setSummary(document.getSummary());
        respVo.setHighlightSummary(firstHighlight(highlights, "summary"));
        respVo.setPrice(document.getPrice());
        respVo.setEpisodeCount(document.getEpisodeCount());
        respVo.setBuyCount(document.getBuyCount());
        respVo.setChargeType(CourseChargeTypeEnum.of(document.getChargeType()));
        return respVo;
    }

    private String firstHighlight(Map<String, List<String>> highlights, String fieldName) {
        List<String> values = highlights.get(fieldName);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private Long calcPages(Long total, Long size) {
        if (total == null || total == 0 || size == null || size == 0) {
            return 0L;
        }
        return (total + size - 1) / size;
    }
}
