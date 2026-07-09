# 1.什么是elasticseatch

**Elasticsearch（简称 ES）** 是一个**分布式全文搜索引擎**

Elasticsearch 是擅长"搜索"的数据库

Index 是存放 Document 的逻辑容器，Document 是一条 JSON 数据，Field 是 Document 中的每个字段；倒排索引则是 Elasticsearch 根据这些 Field 自动建立的底层索引结构，用来实现快速搜索

## 1.1 什么是全文搜索

因为非结构化数据的数据量大且格式不固定，所以我们要用全文检索，全文检索是一种非结构化数据的搜索方式，全文检索通过建立倒排索引加快搜索效率

## 1.2 什么是倒排索引

非结构化数据中我们往往会根据关键词查询数据。此时我们将数据中的关键词建立为索引，指向文档数据，这样的索引称为*倒排索引*。关键字 -> 数据

将文档id建立为索引，通过id快速可以快速查找数据。如数据库中的主键就会创建*正排索引*。id -> 数据

# 2.docker安装elasticsearch

```dockerfile
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.15.3
    container_name: elasticsearch
    restart: always
    environment:
      discovery.type: single-node
      xpack.security.enabled: false
      ES_JAVA_OPTS: "-Xms1g -Xmx1g"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - ./elasticsearch/data:/usr/share/elasticsearch/data
      - ./elasticsearch/plugins:/usr/share/elasticsearch/plugins

  kibana:
    image: docker.elastic.co/kibana/kibana:8.15.3
    container_name: kibana
    restart: always
    environment:
      ELASTICSEARCH_HOSTS: http://elasticsearch:9200
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

Kibana对Elasticsearch索引中的数据进行搜索、查看、交互操作。

## 2.1 IK中文分词器插件安装

让elasticseatch支持中文搜索

```dockerfile
docker exec -it elasticsearch bash
```

```dockerfile
bin/elasticsearch-plugin install https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-8.15.3.zip
```



# 3.怎么使用elasticsearch

## 3.1 如何同步MySql和ES的数据

通常业务数据仍然写入 MySQL，再通过消息队列、定时任务或数据同步工具把需要搜索的数据同步到 ES。

## 3.2 创建索引Index

这个Index就相当于MySql的database，注意，这个不是倒排索引

![image-20260701124127231](../images/image-20260701124127231.png)

## 3.3 创建Document

Document就是ES中存储的一条数据

第一步，打开这个控制台

```bash
PUT /course_index
{
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "categoryName": {
        "type": "keyword"
      },
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "subtitle": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "coverUrl": {
        "type": "keyword",
        "index": false
      },
      "teacherName": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 128
          }
        }
      },
      "summary": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "price": {
        "type": "integer"
      },
      "episodeCount": {
        "type": "integer"
      },
      "buyCount": {
        "type": "integer"
      },
      "sort": {
        "type": "integer"
      },
      "status": {
        "type": "integer"
      },
      "chargeType": {
        "type": "integer"
      },
      "createTime": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis"
      }
    }
  }
}
```

![image-20260701125311439](../images/image-20260701125311439.png)

## 3.4 maven依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
```

## 3.5 将MySql的一条数据放到ES索引里面去

```java
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
```

## 3.6 删除一条Document

```java
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
```

## 3.7 全量重建

```java
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
```

# 3.8 Doucment对象

```java
import lombok.Data;

/**
 * 课程搜索文档。
 */
@Data
public class CourseSearchDocument {

    /**
     * 课程ID ES里使用字符串保存，避免前端Long精度问题
     */
    private String id;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程副标题
     */
    private String subtitle;

    /**
     * 课程封面图URL
     */
    private String coverUrl;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 课程摘要
     */
    private String summary;

    /**
     * 课程售价，单位：分
     */
    private Integer price;

    /**
     * 章节数量
     */
    private Integer episodeCount;

    /**
     * 购买人数/销量
     */
    private Integer buyCount;

    /**
     * 排序值，越大越靠前
     */
    private Integer sort;

    /**
     * 课程状态：0草稿 1上架 2下架
     */
    private Integer status;

    /**
     * 收费类型：0免费 1付费 2VIP免费
     */
    private Integer chargeType;

    /**
     * 创建时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTime;
}
```

# 3.9 搜索逻辑

```java
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
```

```java
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
```

```java
    private void applySort(co.elastic.clients.elasticsearch.core.SearchRequest.Builder search, CourseSearchQuery query) {
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
```

```java
    private CourseSearchPage buildPageResult(SearchResponse<CourseSearchDocument> response, CourseSearchQuery query) {
        CourseSearchPage page = new CourseSearchPage();
        long total = response.hits().total() == null ? 0L : response.hits().total().value();
        page.setTotal(total);
        page.setCurrent(query.getCurrent());
        page.setSize(query.getSize());

        List<CourseSearchPage.CourseSearchRecord> records = new ArrayList<>();
        for (Hit<CourseSearchDocument> hit : response.hits().hits()) {
            if (hit.source() == null) {
                continue;
            }
            CourseSearchPage.CourseSearchRecord record = new CourseSearchPage.CourseSearchRecord();
            record.setDocument(hit.source());
            record.setHighlights(hit.highlight());
            records.add(record);
        }
        page.setRecords(records);
        return page;
    }
```

![image-20260701153400956](../images/image-20260701153400956.png)