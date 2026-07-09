package com.haoclass.main.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.order.admin.request.AdminCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderDetailVo;

public interface AdminCourseOrderApplicationService {

    /**
     * 分页查询订单
     * @param query
     * @return
     */
    PageResult<AdminCourseOrderBasicVo> getCourseOrderPageList(AdminCourseOrderPageQueryReqVo query);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    AdminCourseOrderDetailVo getCourseOrderDetail(Long id);
}
