package com.haoclass.main.application.converter.inner;

import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderAvailableCouponRespVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderAvailableCouponConverter {

    OrderAvailableCouponConverter INSTANCE = Mappers.getMapper(OrderAvailableCouponConverter.class);

    CourseOrderAvailableCouponRespVo innerToRespVo(AvailableCouponResponse availableCouponResponse);

}
