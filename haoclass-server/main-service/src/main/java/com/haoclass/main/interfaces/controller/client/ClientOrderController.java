package com.haoclass.main.interfaces.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.client.ClientCourseOrderApplicationService;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderReqVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderAvailableCouponRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderDetailVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderPayRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderRefundRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端用户订单接口
 */
@Slf4j
@RestController
@RequestMapping("/api/main/client/orders")
@RequiredArgsConstructor
public class ClientOrderController {

    private final ClientCourseOrderApplicationService clientCourseOrderApplicationService;

    /**
     * 用户分页查询订单
     *
     * @param reqVo 分页查询请求对象
     * @return 订单分页结果
     */
    @GetMapping
    public Result<PageResult<CourseOrderBasicVo>> getOrderPageList(@Valid ClientCourseOrderPageQueryReqVo reqVo) {
        log.info("用户分页查询订单，reqVo: {}", JSONObject.toJSONString(reqVo));
        return Result.success(clientCourseOrderApplicationService.getCourseOrderPageList(reqVo));
    }

    /**
     * 用户创建订单
     *
     * @param reqVo 创建订单请求对象
     * @return 创建成功的订单信息
     */
    @PostMapping
    public Result<CourseOrderBasicVo> createOrder(@RequestBody @Valid ClientCourseOrderReqVo reqVo) {
        log.info("用户创建订单，reqVo: {}", JSONObject.toJSONString(reqVo));
        return Result.success(clientCourseOrderApplicationService.createCourseOrder(reqVo));
    }

    /**
     * 用户查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("{id}")
    public Result<CourseOrderDetailVo> getCourseOrderDetail(@PathVariable("id") Long id) {
        return Result.success(clientCourseOrderApplicationService.getCourseOrderDetail(id));
    }

    /**
     * 用户模拟支付订单
     *
     * @param id 订单ID
     * @return 订单支付结果
     */
    @PostMapping("{id}/mock-pay")
    public Result<CourseOrderPayRespVo> pay(@PathVariable("id") Long id) {
        return Result.success(clientCourseOrderApplicationService.payCourseOrder(id));
    }

    /**
     * 用户模拟退款订单
     *
     * @param id 订单ID
     * @return 订单退款结果
     */
    @PostMapping("{id}/mock-refund")
    public Result<CourseOrderRefundRespVo> refund(@PathVariable("id") Long id) {
        return Result.success(clientCourseOrderApplicationService.refundCourseOrder(id));
    }

    /**
     * 用户取消订单
     *
     * @param id 订单ID
     * @return 无返回数据
     */
    @PostMapping("{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable("id") Long id) {
        clientCourseOrderApplicationService.cancelCourseOrder(id);
        return Result.success();
    }

    /**
     * 查询购买指定课程时可用的优惠券
     *
     * @param courseId 课程ID
     * @return 可用优惠券列表
     */
    @GetMapping("/available")
    public Result<List<CourseOrderAvailableCouponRespVo>> getAvailableList(@RequestParam("courseId") Long courseId) {
        return clientCourseOrderApplicationService.getAvailableList(courseId);
    }
}
