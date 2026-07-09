package com.haoclass.main.interfaces.vo.course.client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAccessVo {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 是否拥有课程权益
     */
    private Boolean owned;

    /**
     * 是否可以观看完整课程
     */
    private Boolean canWatchFull;
}