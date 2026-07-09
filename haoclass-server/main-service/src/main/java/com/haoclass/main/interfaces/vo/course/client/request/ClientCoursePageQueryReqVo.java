package com.haoclass.main.interfaces.vo.course.client.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCoursePageQueryReqVo {

    /**
     * 当前页码，默认从1开始
     */
    @Min(value = 1, message = "当前页不能小于1")
    private Long current = 1L;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    private Long size = 10L;

    /**
     * 课程分类名称
     */
    private String categoryName;

    /**
     * 课程标题，支持模糊查询
     */
    private String title;
}