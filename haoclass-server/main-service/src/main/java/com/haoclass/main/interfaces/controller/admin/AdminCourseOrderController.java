package com.haoclass.main.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminCourseOrderApplicationService;
import com.haoclass.main.interfaces.vo.order.admin.request.AdminCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理-admin端-订单管理
 *
 * @author zhanjunhao
 * @since 2026/06/02
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/admin/orders")
public class AdminCourseOrderController {

    private final AdminCourseOrderApplicationService adminCourseOrderApplicationService;

    /**
     * 分页查询订单
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<AdminCourseOrderBasicVo>> getCourseOrderPageList(
            @Valid AdminCourseOrderPageQueryReqVo query) {
        log.info("后台分页查询课程订单, query: {}", JSONObject.toJSONString(query));
        return Result.success(adminCourseOrderApplicationService.getCourseOrderPageList(query));
    }

    /**
     * 课程订单详情
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<AdminCourseOrderDetailVo> getCourseOrderDetail(@PathVariable("id") Long id) {
        log.info("后台查询课程订单详情, id: {}", id);
        return Result.success(adminCourseOrderApplicationService.getCourseOrderDetail(id));
    }
}
