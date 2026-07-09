package com.haoclass.coupon.application.converter.inner;

import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface InnerUserCouponConverter {

    InnerUserCouponConverter INSTANCE = Mappers.getMapper(InnerUserCouponConverter.class);

    @Mapping(source = "id", target = "userCouponId")
    AvailableCouponResponse poToResponse(UserCoupon po);

    List<AvailableCouponResponse> poToResponse(List<UserCoupon> po);

}
