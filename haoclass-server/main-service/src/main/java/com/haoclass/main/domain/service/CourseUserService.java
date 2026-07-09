package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseUserService extends IService<CourseUser> {

    /**
     * 查询拥有对应课程的权益用户
     * @param userId
     * @param courseId
     * @return
     */
    CourseUser findValidByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * 查询用户对应课程的权益
     * @param userId
     * @param courseId
     * @return
     */
    CourseUser findByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * 查看用户拥有的权益
     * @param userId
     * @return
     */
    List<CourseUser> listValidByUserId(Long userId);

    /**
     * 添加用户权益
     * @param courseUser
     */
    void addCourseUser(CourseUser courseUser);

    /**
     * 更新用户权益
     * @param courseUser
     */
    void updateCourseUser(CourseUser courseUser);

    /**
     * 退款后回收购买课程权益
     *
     * @param userId     用户ID
     * @param courseId   课程ID
     * @param orderId    来源订单ID
     * @param refundTime 退款时间
     */
    void refundCourseUser(Long userId, Long courseId, Long orderId, LocalDateTime refundTime);
}
