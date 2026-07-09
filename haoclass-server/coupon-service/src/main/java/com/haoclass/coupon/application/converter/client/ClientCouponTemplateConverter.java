package com.haoclass.coupon.application.converter.client;

import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.client.response.ClientCouponTemplateBasicVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientCouponTemplateConverter {

    ClientCouponTemplateConverter INSTANCE = Mappers.getMapper(ClientCouponTemplateConverter.class);

    ClientCouponTemplateBasicVo poToBasicVo(CouponTemplate po);

    List<ClientCouponTemplateBasicVo> poToBasicVo(List<CouponTemplate> po);
}
