package com.haoclass.coupon.application.converter.command;

import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateUpdateReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CouponTemplateCommandConverter {

    CouponTemplateCommandConverter INSTANCE = Mappers.getMapper(CouponTemplateCommandConverter.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receivedCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createdUser", ignore = true)
    @Mapping(target = "updatedUser", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    CouponTemplate createReqVoToPo(AdminCouponTemplateReqVo reqVo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "receivedCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createdUser", ignore = true)
    @Mapping(target = "updatedUser", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    CouponTemplate updateReqVoToPo(AdminCouponTemplateUpdateReqVo reqVo);
}
