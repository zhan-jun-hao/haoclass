package com.haoclass.main.interfaces.vo.course.client.request;

import com.haoclass.common.query.PageQuery;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C端课程搜索请求对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientCourseSearchReqVo extends PageQuery {

    /**
     * 搜索关键词，匹配课程标题、副标题、摘要、讲师名称等字段。
     */
    private String keyword;

    /**
     * 课程分类名称。
     */
    private String categoryName;

    /**
     * 收费类型：0免费 1付费 2VIP免费。
     */
    private Integer chargeType;

    /**
     * 排序类型：0综合 1最新 2销量 3价格升序 4价格降序。
     */
    private Integer sortType = 0;
}
