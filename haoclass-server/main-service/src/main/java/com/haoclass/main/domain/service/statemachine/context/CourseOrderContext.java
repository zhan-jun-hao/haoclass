package com.haoclass.main.domain.service.statemachine.context;

import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOrderContext {

    /**
     * 订单Id
     */
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 课程Id
     */
    private Long courseId;

    /**
     * 当前订单状态
     */
    private CourseOrderStatusEnum currentStatus;
}
