package com.haoclass.main.application.converter.inner;

import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderAvailableCouponRespVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class OrderAvailableCouponConverterImpl implements OrderAvailableCouponConverter {

    @Override
    public CourseOrderAvailableCouponRespVo innerToRespVo(AvailableCouponResponse availableCouponResponse) {
        if ( availableCouponResponse == null ) {
            return null;
        }

        CourseOrderAvailableCouponRespVo courseOrderAvailableCouponRespVo = new CourseOrderAvailableCouponRespVo();

        courseOrderAvailableCouponRespVo.setUserCouponId( availableCouponResponse.getUserCouponId() );
        courseOrderAvailableCouponRespVo.setCouponName( availableCouponResponse.getCouponName() );
        courseOrderAvailableCouponRespVo.setThresholdAmount( availableCouponResponse.getThresholdAmount() );
        courseOrderAvailableCouponRespVo.setDiscountAmount( availableCouponResponse.getDiscountAmount() );
        courseOrderAvailableCouponRespVo.setValidEndTime( availableCouponResponse.getValidEndTime() );

        return courseOrderAvailableCouponRespVo;
    }
}
