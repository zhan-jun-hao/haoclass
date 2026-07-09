package com.haoclass.coupon.application.converter.client;

import com.haoclass.coupon.domain.model.query.UserCouponQuery;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import com.haoclass.coupon.interfaces.vo.client.request.ClientMyCouponPageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientMyCouponBasicVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:38+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientMyCouponConverterImpl implements ClientMyCouponConverter {

    @Override
    public UserCouponQuery reqVoToQuery(ClientMyCouponPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        UserCouponQuery userCouponQuery = new UserCouponQuery();

        userCouponQuery.setCurrent( reqVo.getCurrent() );
        userCouponQuery.setSize( reqVo.getSize() );
        userCouponQuery.setStatus( reqVo.getStatus() );

        return userCouponQuery;
    }

    @Override
    public List<ClientMyCouponBasicVo> poToBasicVo(List<UserCoupon> poList) {
        if ( poList == null ) {
            return null;
        }

        List<ClientMyCouponBasicVo> list = new ArrayList<ClientMyCouponBasicVo>( poList.size() );
        for ( UserCoupon userCoupon : poList ) {
            list.add( userCouponToClientMyCouponBasicVo( userCoupon ) );
        }

        return list;
    }

    protected ClientMyCouponBasicVo userCouponToClientMyCouponBasicVo(UserCoupon userCoupon) {
        if ( userCoupon == null ) {
            return null;
        }

        ClientMyCouponBasicVo clientMyCouponBasicVo = new ClientMyCouponBasicVo();

        clientMyCouponBasicVo.setId( userCoupon.getId() );
        clientMyCouponBasicVo.setCouponTemplateId( userCoupon.getCouponTemplateId() );
        clientMyCouponBasicVo.setCouponName( userCoupon.getCouponName() );
        clientMyCouponBasicVo.setThresholdAmount( userCoupon.getThresholdAmount() );
        clientMyCouponBasicVo.setDiscountAmount( userCoupon.getDiscountAmount() );
        clientMyCouponBasicVo.setStatus( userCoupon.getStatus() );
        clientMyCouponBasicVo.setOrderId( userCoupon.getOrderId() );
        clientMyCouponBasicVo.setLockTime( userCoupon.getLockTime() );
        clientMyCouponBasicVo.setLockExpireTime( userCoupon.getLockExpireTime() );
        clientMyCouponBasicVo.setUseTime( userCoupon.getUseTime() );
        clientMyCouponBasicVo.setValidStartTime( userCoupon.getValidStartTime() );
        clientMyCouponBasicVo.setValidEndTime( userCoupon.getValidEndTime() );
        clientMyCouponBasicVo.setCreateTime( userCoupon.getCreateTime() );

        return clientMyCouponBasicVo;
    }
}
