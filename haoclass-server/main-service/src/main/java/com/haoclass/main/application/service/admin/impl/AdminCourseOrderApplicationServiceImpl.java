package com.haoclass.main.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.admin.AdminCourseOrderConverter;
import com.haoclass.main.application.service.admin.AdminCourseOrderApplicationService;
import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.domain.service.CourseOrderService;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.interfaces.vo.order.admin.request.AdminCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCourseOrderApplicationServiceImpl implements AdminCourseOrderApplicationService {

    private final CourseOrderService courseOrderService;

    @Override
    public PageResult<AdminCourseOrderBasicVo> getCourseOrderPageList(AdminCourseOrderPageQueryReqVo query) {
        CourseOrderQuery courseOrderQuery = AdminCourseOrderConverter.INSTANCE.reqVoToQuery(query);
        IPage<CourseOrder> page = courseOrderService.pageQuery(courseOrderQuery);
        List<AdminCourseOrderBasicVo> records =
                AdminCourseOrderConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public AdminCourseOrderDetailVo getCourseOrderDetail(Long id) {
        CourseOrder courseOrder = courseOrderService.getCourseOrderById(id);
        return AdminCourseOrderConverter.INSTANCE.poToDetailVo(courseOrder);
    }
}
