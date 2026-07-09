package com.haoclass.main.application.converter.client;

import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientCourseOrderConverter {

    ClientCourseOrderConverter INSTANCE = Mappers.getMapper(ClientCourseOrderConverter.class);

    List<CourseOrderBasicVo> poToBasicVo(List<CourseOrder> courseOrderList);

    CourseOrderBasicVo poToBasicVo(CourseOrder courseOrder);

    CourseOrderDetailVo poToDetailVo(CourseOrder courseOrder);

    CourseOrderQuery reqVoToQuery(ClientCourseOrderPageQueryReqVo reqVo);
}
