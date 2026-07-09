package com.haoclass.coupon.application.converter.inner;

import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:38+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class InnerUserCouponConverterImpl implements InnerUserCouponConverter {

    @Override
    public AvailableCouponResponse poToResponse(UserCoupon po) {
        if ( po == null ) {
            return null;
        }

        AvailableCouponResponse availableCouponResponse = new AvailableCouponResponse();

        availableCouponResponse.setUserCouponId( po.getId() );
        availableCouponResponse.setCouponName( po.getCouponName() );
        availableCouponResponse.setThresholdAmount( po.getThresholdAmount() );
        availableCouponResponse.setDiscountAmount( po.getDiscountAmount() );
        availableCouponResponse.setValidEndTime( po.getValidEndTime() );

        return availableCouponResponse;
    }

    @Override
    public List<AvailableCouponResponse> poToResponse(List<UserCoupon> po) {
        if ( po == null ) {
            return null;
        }

        List<AvailableCouponResponse> list = new ArrayList<AvailableCouponResponse>( po.size() );
        for ( UserCoupon userCoupon : po ) {
            list.add( poToResponse( userCoupon ) );
        }

        return list;
    }
}
