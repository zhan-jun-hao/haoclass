package com.haoclass.coupon.application.converter.admin;

import com.haoclass.coupon.domain.model.query.CouponTemplateQuery;
import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplatePageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateBasicVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminCouponTemplateConverter {

    AdminCouponTemplateConverter INSTANCE = Mappers.getMapper(AdminCouponTemplateConverter.class);

    CouponTemplateQuery pageReqVoToQuery(AdminCouponTemplatePageQueryReqVo reqVo);

    List<AdminCouponTemplateBasicVo> poToBasicVo(List<CouponTemplate> poList);

    AdminCouponTemplateDetailVo poToDetailVo(CouponTemplate po);
}
