package com.haoclass.main.interfaces.vo.course.admin.request;

import com.haoclass.common.query.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCoursePageQueryReqVo extends PageQuery {

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程标题，支持模糊查询
     */
    private String title;

    /**
     * 课程状态：0草稿 1上架 2下架
     */
    private Integer status;
}