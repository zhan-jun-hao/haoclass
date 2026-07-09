package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.CourseUserSourceTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户课程权益表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("course_user")
public class CourseUser extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 来源订单ID
     */
    private Long orderId;

    /**
     * 来源：0购买 1后台赠送 2免费领取
     */
    private CourseUserSourceTypeEnum sourceType;

    /**
     * 权益状态：0失效 1有效 2退款失效
     */
    private CourseUserStatusEnum status;

    /**
     * 获得权益时间
     */
    private LocalDateTime grantTime;

    /**
     * 权益过期时间，NULL表示永久有效
     */
    private LocalDateTime expireTime;

}
