package com.haoclass.main.application.service.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderReqVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderAvailableCouponRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderDetailVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderPayRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderRefundRespVo;

import java.util.List;

public interface ClientCourseOrderApplicationService {

    /**
     * 创建课程订单
     *
     * @param reqVo 创建课程订单请求对象
     * @return 课程订单基础信息
     */
    CourseOrderBasicVo createCourseOrder(ClientCourseOrderReqVo reqVo);

    /**
     * 查询课程订单详情
     *
     * @param id 订单ID
     * @return 课程订单详情
     */
    CourseOrderDetailVo getCourseOrderDetail(Long id);

    /**
     * 支付课程订单
     *
     * @param id 订单ID
     * @return 课程订单支付结果
     */
    CourseOrderPayRespVo payCourseOrder(Long id);

    /**
     * 申请课程订单退款
     *
     * @param id 订单ID
     * @return 课程订单退款结果
     */
    CourseOrderRefundRespVo refundCourseOrder(Long id);

    /**
     * 用户分页查询课程订单
     *
     * @param reqVo 分页查询请求对象
     * @return 课程订单分页结果
     */
    PageResult<CourseOrderBasicVo> getCourseOrderPageList(ClientCourseOrderPageQueryReqVo reqVo);

    /**
     * 用户取消课程订单
     *
     * @param id 订单ID
     */
    void cancelCourseOrder(Long id);

    /**
     * 查询课程订单可用优惠券
     *
     * @param courseId 课程ID
     * @return 可用优惠券列表
     */
    Result<List<CourseOrderAvailableCouponRespVo>> getAvailableList(Long courseId);
}
