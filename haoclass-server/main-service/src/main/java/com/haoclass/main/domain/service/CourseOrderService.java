package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 订单接口
 */
public interface CourseOrderService extends IService<CourseOrder> {

    /**
     * 后台分页查询订单
     */
    IPage<CourseOrder> pageQuery(CourseOrderQuery query);

    /**
     * 查询订单，不限制所属用户
     */
    CourseOrder getCourseOrderById(Long id);

    /**
     * 查询订单
     *
     * @param id
     * @param userId
     * @return
     */
    CourseOrder getCourseOrderById(Long id, Long userId);

    /**
     * 返回待支付订单
     *
     * @param userId
     * @param courseId
     * @return
     */
    CourseOrder findPendingOrder(Long userId, Long courseId);

    void addCourseOrder(CourseOrder courseOrder);

    /**
     * 获取未支付的订单
     *
     * @param id
     * @param userId
     * @return
     */
    CourseOrder getPendingOrderById(Long id, Long userId);

    /**
     * 支付待支付订单
     *
     * @param id
     * @param userId
     * @param thirdTradeNo
     * @param payType
     * @param payTime
     */
    void payPendingOrder(Long id, Long userId, String thirdTradeNo, CourseOrderPayTypeEnum payType,
                         LocalDateTime payTime, CourseOrder pendingOrder);

    /**
     * 退款已支付订单
     *
     * @param id          订单ID
     * @param userId      用户ID
     * @param refundTime  退款时间
     * @param paidOrder   当前已支付订单
     */
    void refundPaidOrder(Long id, Long userId, LocalDateTime refundTime, CourseOrder paidOrder);

    /**
     * 批量关闭订单
     *
     * @param now
     */
    void closeExpiredPendingOrders(Set<Long> ids, LocalDateTime now);

    /**
     * 根据支付关闭消息关闭待支付课程订单。
     *
     * @param id        课程订单ID
     * @param closeTime 关闭时间
     * @return 是否关闭成功
     */
    boolean closePendingOrderByPaymentClosed(Long id, LocalDateTime closeTime);

    /**
     * 关闭待支付订单
     *
     * @param now
     * @param id
     */
    void closePendingOrders(LocalDateTime now, Long id);

    /**
     * 批量查询未支付订单
     *
     * @param now
     * @return
     */
    List<CourseOrder> findPendingOrders(LocalDateTime now);
}
