package com.haoclass.coupon.application.converter.command;

import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateUpdateReqVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:38+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class CouponTemplateCommandConverterImpl implements CouponTemplateCommandConverter {

    @Override
    public CouponTemplate createReqVoToPo(AdminCouponTemplateReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CouponTemplate couponTemplate = new CouponTemplate();

        couponTemplate.setCouponName( reqVo.getCouponName() );
        couponTemplate.setDescription( reqVo.getDescription() );
        couponTemplate.setThresholdAmount( reqVo.getThresholdAmount() );
        couponTemplate.setDiscountAmount( reqVo.getDiscountAmount() );
        couponTemplate.setTotalStock( reqVo.getTotalStock() );
        couponTemplate.setReceiveStartTime( reqVo.getReceiveStartTime() );
        couponTemplate.setReceiveEndTime( reqVo.getReceiveEndTime() );
        couponTemplate.setValidStartTime( reqVo.getValidStartTime() );
        couponTemplate.setValidEndTime( reqVo.getValidEndTime() );

        return couponTemplate;
    }

    @Override
    public CouponTemplate updateReqVoToPo(AdminCouponTemplateUpdateReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CouponTemplate couponTemplate = new CouponTemplate();

        couponTemplate.setCouponName( reqVo.getCouponName() );
        couponTemplate.setDescription( reqVo.getDescription() );
        couponTemplate.setThresholdAmount( reqVo.getThresholdAmount() );
        couponTemplate.setDiscountAmount( reqVo.getDiscountAmount() );
        couponTemplate.setTotalStock( reqVo.getTotalStock() );
        couponTemplate.setReceiveStartTime( reqVo.getReceiveStartTime() );
        couponTemplate.setReceiveEndTime( reqVo.getReceiveEndTime() );
        couponTemplate.setValidStartTime( reqVo.getValidStartTime() );
        couponTemplate.setValidEndTime( reqVo.getValidEndTime() );

        return couponTemplate;
    }
}
