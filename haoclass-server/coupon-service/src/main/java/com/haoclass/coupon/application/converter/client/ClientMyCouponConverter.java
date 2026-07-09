package com.haoclass.coupon.application.converter.client;

import com.haoclass.coupon.domain.model.query.UserCouponQuery;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import com.haoclass.coupon.interfaces.vo.client.request.ClientMyCouponPageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientMyCouponBasicVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * C端我的优惠券对象转换器
 */
@Mapper
public interface ClientMyCouponConverter {

    ClientMyCouponConverter INSTANCE = Mappers.getMapper(ClientMyCouponConverter.class);

    UserCouponQuery reqVoToQuery(ClientMyCouponPageQueryReqVo reqVo);

    List<ClientMyCouponBasicVo> poToBasicVo(List<UserCoupon> poList);
}
