package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.interfaces.vo.order.admin.request.AdminCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminCourseOrderConverter {

    AdminCourseOrderConverter INSTANCE = Mappers.getMapper(AdminCourseOrderConverter.class);

    List<AdminCourseOrderBasicVo> poToBasicVo(List<CourseOrder> courseOrderList);

    AdminCourseOrderDetailVo poToDetailVo(CourseOrder courseOrder);

    CourseOrderQuery reqVoToQuery(AdminCourseOrderPageQueryReqVo reqVo);
}
