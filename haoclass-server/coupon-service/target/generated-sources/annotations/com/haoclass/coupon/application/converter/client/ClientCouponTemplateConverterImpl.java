package com.haoclass.coupon.application.converter.client;

import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.client.response.ClientCouponTemplateBasicVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:38+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientCouponTemplateConverterImpl implements ClientCouponTemplateConverter {

    @Override
    public ClientCouponTemplateBasicVo poToBasicVo(CouponTemplate po) {
        if ( po == null ) {
            return null;
        }

        ClientCouponTemplateBasicVo clientCouponTemplateBasicVo = new ClientCouponTemplateBasicVo();

        clientCouponTemplateBasicVo.setId( po.getId() );
        clientCouponTemplateBasicVo.setCouponName( po.getCouponName() );
        clientCouponTemplateBasicVo.setThresholdAmount( po.getThresholdAmount() );
        clientCouponTemplateBasicVo.setDiscountAmount( po.getDiscountAmount() );
        clientCouponTemplateBasicVo.setTotalStock( po.getTotalStock() );
        clientCouponTemplateBasicVo.setReceiveStartTime( po.getReceiveStartTime() );
        clientCouponTemplateBasicVo.setReceiveEndTime( po.getReceiveEndTime() );
        clientCouponTemplateBasicVo.setValidEndTime( po.getValidEndTime() );
        clientCouponTemplateBasicVo.setStatus( po.getStatus() );
        clientCouponTemplateBasicVo.setCreateTime( po.getCreateTime() );
        clientCouponTemplateBasicVo.setUpdateTime( po.getUpdateTime() );

        return clientCouponTemplateBasicVo;
    }

    @Override
    public List<ClientCouponTemplateBasicVo> poToBasicVo(List<CouponTemplate> po) {
        if ( po == null ) {
            return null;
        }

        List<ClientCouponTemplateBasicVo> list = new ArrayList<ClientCouponTemplateBasicVo>( po.size() );
        for ( CouponTemplate couponTemplate : po ) {
            list.add( poToBasicVo( couponTemplate ) );
        }

        return list;
    }
}
