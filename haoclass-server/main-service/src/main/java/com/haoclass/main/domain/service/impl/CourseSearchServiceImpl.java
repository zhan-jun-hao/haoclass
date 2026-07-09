package com.haoclass.main.domain.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.model.query.CourseSearchPage;
import com.haoclass.main.domain.model.query.CourseSearchQuery;
import com.haoclass.main.domain.model.query.CourseSearchRecord;
import com.haoclass.main.domain.service.CourseSearchService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.infrastructure.common.enums.CourseStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.search.document.CourseSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 课程搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchServiceImpl implements CourseSearchService {

    private static final String COURSE_INDEX = "course_index";

    private static final int REBUILD_BATCH_SIZE = 500;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ElasticsearchClient elasticsearchClient;

    private final CourseService courseService;

    @Override
    public Integer rebuildPublishedCourseIndex() {
        clearCourseIndex();

        int total = 0;
        long current = 1L;
        // 利用分页查询处理 一页一页重建
        while (true) {
            LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery(Course.class)
                    .eq(Course::getDeleted, 0)
                    .eq(Course::getStatus, CourseStatusEnum.PUBLISHED)
                    .orderByAsc(Course::getId);
            IPage<Course> page = courseService.page(new Page<>(current, REBUILD_BATCH_SIZE), wrapper);
            if (page.getRecords().isEmpty()) {
                break;
            }

            for (Course course : page.getRecords()) {
                indexCourse(course);
                total++;
            }

            if (current >= page.getPages()) {
                break;
            }
            current++;
        }

        return total;
    }

    @Override
    public void syncCourseIndex(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        if (isPublished(course)) {
            indexCourse(course);
        } else {
            deleteCourseIndex(courseId);
        }
    }

    @Override
    public void deleteCourseIndex(Long courseId) {
        try {
            elasticsearchClient.delete(delete -> delete
                    .index(COURSE_INDEX)
                    .id(String.valueOf(courseId))
                    .refresh(Refresh.True));
        } catch (ElasticsearchException e) {
            if (e.status() == 404) {
                return;
            }
            throw new BusinessException("删除课程搜索索引失败");
        } catch (IOException e) {
            throw new BusinessException("删除课程搜索索引失败");
        }
    }

    @Override
    public CourseSearchPage searchPublishedCourses(CourseSearchQuery query) {
        CourseSearchQuery safeQuery = normalizeQuery(query);
        int from = Math.toIntExact((safeQuery.getCurrent() - 1) * safeQuery.getSize());
        int size = Math.toIntExact(safeQuery.getSize());
        try {
            SearchResponse<CourseSearchDocument> response = elasticsearchClient.search(search -> {
                search.index(COURSE_INDEX)
                        .from(from)
                        .size(size)
                        .query(q -> q.bool(bool -> buildQuery(bool, safeQuery)))
                        .highlight(highlight -> highlight
                                .preTags("<em>")
                                .postTags("</em>")
                                .fields("title", field -> field)
                                .fields("subtitle", field -> field)
                                .fields("summary", field -> field));

                applySort(search, safeQuery);
                return search;
            }, CourseSearchDocument.class);

            return buildPageResult(response, safeQuery);
        } catch (ElasticsearchException e) {
            log.error("课程搜索失败, query: {}", safeQuery, e);
            throw new BusinessException("课程搜索失败，请确认Elasticsearch索引已创建");
        } catch (IOException e) {
            log.error("课程搜索失败, query: {}", safeQuery, e);
            throw new BusinessException("课程搜索失败，请稍后重试");
        }
    }

    private BoolQuery.Builder buildQuery(BoolQuery.Builder bool, CourseSearchQuery query) {
        bool.filter(filter ->
                filter.term(term -> term
                        .field("status")
                        .value(CourseStatusEnum.PUBLISHED.getCode())));

        if (StringUtils.hasText(query.getCategoryName())) {
            bool.filter(filter -> filter
                    .term(term -> term.field("categoryName")
                            .value(query.getCategoryName())));
        }

        if (query.getChargeType() != null) {
            bool.filter(filter -> filter
                    .term(term -> term.field("chargeType")
                            .value(query.getChargeType())));
        }

        if (StringUtils.hasText(query.getKeyword())) {
            bool.must(must -> must
                    .multiMatch(multiMatch -> multiMatch
                            .query(query.getKeyword())
                            // ^3表示的是关键词的权重
                            .fields("title^3", "subtitle^2", "summary", "teacherName", "categoryName")));
        } else {
            bool.must(must -> must
                    .matchAll(matchAll -> matchAll));
        }

        return bool;
    }

    private void applySort(SearchRequest.Builder search, CourseSearchQuery query) {
        int sortType = query.getSortType() == null ? 0 : query.getSortType();
        switch (sortType) {
            case 1 -> search.sort(sort -> sort.field(field -> field.field("createTime").order(SortOrder.Desc)));
            case 2 -> search.sort(sort -> sort.field(field -> field.field("buyCount").order(SortOrder.Desc)))
                    .sort(sort -> sort.field(field -> field.field("sort").order(SortOrder.Desc)));
            case 3 -> search.sort(sort -> sort.field(field -> field.field("price").order(SortOrder.Asc)));
            case 4 -> search.sort(sort -> sort.field(field -> field.field("price").order(SortOrder.Desc)));
            default -> {
                if (StringUtils.hasText(query.getKeyword())) {
                    search.sort(sort -> sort.score(score -> score.order(SortOrder.Desc)));
                }
                search.sort(sort -> sort.field(field -> field.field("sort").order(SortOrder.Desc)))
                        .sort(sort -> sort.field(field -> field.field("createTime").order(SortOrder.Desc)));
            }
        }
    }

    private CourseSearchPage buildPageResult(SearchResponse<CourseSearchDocument> response, CourseSearchQuery query) {
        CourseSearchPage page = new CourseSearchPage();
        long total = response.hits().total() == null ? 0L : response.hits().total().value();
        page.setTotal(total);
        page.setCurrent(query.getCurrent());
        page.setSize(query.getSize());

        List<CourseSearchRecord> records = new ArrayList<>();
        for (Hit<CourseSearchDocument> hit : response.hits().hits()) {
            if (hit.source() == null) {
                continue;
            }
            CourseSearchRecord record = new CourseSearchRecord();
            record.setDocument(hit.source());
            record.setHighlights(hit.highlight());
            records.add(record);
        }
        page.setRecords(records);
        return page;
    }

    private void clearCourseIndex() {
        try {
            elasticsearchClient.deleteByQuery(delete -> delete
                    .index(COURSE_INDEX)
                    .query(query -> query.matchAll(matchAll -> matchAll))
                    .refresh(true));
        } catch (ElasticsearchException e) {
            if (e.status() == 404) {
                throw BusinessException.badRequest("课程搜索索引不存在，请先创建course_index");
            }
            throw new BusinessException("清空课程搜索索引失败");
        } catch (IOException e) {
            throw new BusinessException("清空课程搜索索引失败");
        }
    }

    private void indexCourse(Course course) {
        try {
            CourseSearchDocument document = toDocument(course);
            elasticsearchClient.index(index -> index
                    .index(COURSE_INDEX)
                    .id(document.getId())
                    .document(document)
                    .refresh(Refresh.True));
        } catch (ElasticsearchException e) {
            if (e.status() == 404) {
                throw BusinessException.badRequest("课程搜索索引不存在，请先创建course_index");
            }
            throw new BusinessException("同步课程搜索索引失败");
        } catch (IOException e) {
            throw new BusinessException("同步课程搜索索引失败");
        }
    }

    private CourseSearchDocument toDocument(Course course) {
        CourseSearchDocument document = new CourseSearchDocument();
        document.setId(String.valueOf(course.getId()));
        document.setCategoryName(course.getCategoryName());
        document.setTitle(course.getTitle());
        document.setSubtitle(course.getSubtitle());
        document.setCoverUrl(course.getCoverUrl());
        document.setTeacherName(course.getTeacherName());
        document.setSummary(course.getSummary());
        document.setPrice(course.getPrice());
        document.setEpisodeCount(course.getEpisodeCount());
        document.setBuyCount(course.getBuyCount());
        document.setSort(course.getSort());
        document.setStatus(course.getStatus() == null ? null : course.getStatus().getCode());
        document.setChargeType(course.getChargeType() == null ? null : course.getChargeType().getCode());
        document.setCreateTime(course.getCreateTime() == null ? null : course.getCreateTime().format(DATE_TIME_FORMATTER));
        return document;
    }

    private CourseSearchQuery normalizeQuery(CourseSearchQuery query) {
        CourseSearchQuery safeQuery = query == null ? new CourseSearchQuery() : query;
        if (StringUtils.hasText(safeQuery.getKeyword())) {
            safeQuery.setKeyword(safeQuery.getKeyword().trim());
        }
        if (StringUtils.hasText(safeQuery.getCategoryName())) {
            safeQuery.setCategoryName(safeQuery.getCategoryName().trim());
        }
        return safeQuery;
    }

    private boolean isPublished(Course course) {
        return Objects.equals(course.getDeleted(), 0)
                && Objects.equals(course.getStatus(), CourseStatusEnum.PUBLISHED);
    }
}
