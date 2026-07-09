package com.haoclass.pay.application.converter.admin;

import com.haoclass.pay.domain.model.query.PaymentOrderQuery;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.admin.request.AdminPaymentOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderBasicVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:28+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminPaymentOrderConverterImpl implements AdminPaymentOrderConverter {

    @Override
    public PaymentOrderQuery reqVoToQuery(AdminPaymentOrderPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        PaymentOrderQuery paymentOrderQuery = new PaymentOrderQuery();

        paymentOrderQuery.setCurrent( reqVo.getCurrent() );
        paymentOrderQuery.setSize( reqVo.getSize() );
        paymentOrderQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        paymentOrderQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );
        paymentOrderQuery.setPaymentNo( reqVo.getPaymentNo() );
        paymentOrderQuery.setBizType( reqVo.getBizType() );
        paymentOrderQuery.setBizOrderNo( reqVo.getBizOrderNo() );
        paymentOrderQuery.setUserId( reqVo.getUserId() );
        paymentOrderQuery.setPayChannel( reqVo.getPayChannel() );
        paymentOrderQuery.setStatus( reqVo.getStatus() );
        paymentOrderQuery.setThirdTradeNo( reqVo.getThirdTradeNo() );

        return paymentOrderQuery;
    }

    @Override
    public List<AdminPaymentOrderBasicVo> poToBasicVo(List<PaymentOrder> paymentOrders) {
        if ( paymentOrders == null ) {
            return null;
        }

        List<AdminPaymentOrderBasicVo> list = new ArrayList<AdminPaymentOrderBasicVo>( paymentOrders.size() );
        for ( PaymentOrder paymentOrder : paymentOrders ) {
            list.add( paymentOrderToAdminPaymentOrderBasicVo( paymentOrder ) );
        }

        return list;
    }

    @Override
    public AdminPaymentOrderDetailVo poToDetailVo(PaymentOrder paymentOrder) {
        if ( paymentOrder == null ) {
            return null;
        }

        AdminPaymentOrderDetailVo adminPaymentOrderDetailVo = new AdminPaymentOrderDetailVo();

        adminPaymentOrderDetailVo.setId( paymentOrder.getId() );
        adminPaymentOrderDetailVo.setPaymentNo( paymentOrder.getPaymentNo() );
        adminPaymentOrderDetailVo.setBizType( paymentOrder.getBizType() );
        adminPaymentOrderDetailVo.setBizOrderId( paymentOrder.getBizOrderId() );
        adminPaymentOrderDetailVo.setBizOrderNo( paymentOrder.getBizOrderNo() );
        adminPaymentOrderDetailVo.setUserId( paymentOrder.getUserId() );
        adminPaymentOrderDetailVo.setSubject( paymentOrder.getSubject() );
        adminPaymentOrderDetailVo.setPayAmount( paymentOrder.getPayAmount() );
        adminPaymentOrderDetailVo.setCurrency( paymentOrder.getCurrency() );
        adminPaymentOrderDetailVo.setPayChannel( paymentOrder.getPayChannel() );
        adminPaymentOrderDetailVo.setStatus( paymentOrder.getStatus() );
        adminPaymentOrderDetailVo.setThirdTradeNo( paymentOrder.getThirdTradeNo() );
        adminPaymentOrderDetailVo.setFailureReason( paymentOrder.getFailureReason() );
        adminPaymentOrderDetailVo.setExpireTime( paymentOrder.getExpireTime() );
        adminPaymentOrderDetailVo.setPayTime( paymentOrder.getPayTime() );
        adminPaymentOrderDetailVo.setCloseTime( paymentOrder.getCloseTime() );
        adminPaymentOrderDetailVo.setCreateTime( paymentOrder.getCreateTime() );
        adminPaymentOrderDetailVo.setUpdateTime( paymentOrder.getUpdateTime() );

        return adminPaymentOrderDetailVo;
    }

    protected AdminPaymentOrderBasicVo paymentOrderToAdminPaymentOrderBasicVo(PaymentOrder paymentOrder) {
        if ( paymentOrder == null ) {
            return null;
        }

        AdminPaymentOrderBasicVo adminPaymentOrderBasicVo = new AdminPaymentOrderBasicVo();

        adminPaymentOrderBasicVo.setId( paymentOrder.getId() );
        adminPaymentOrderBasicVo.setPaymentNo( paymentOrder.getPaymentNo() );
        adminPaymentOrderBasicVo.setBizType( paymentOrder.getBizType() );
        adminPaymentOrderBasicVo.setBizOrderNo( paymentOrder.getBizOrderNo() );
        adminPaymentOrderBasicVo.setUserId( paymentOrder.getUserId() );
        adminPaymentOrderBasicVo.setSubject( paymentOrder.getSubject() );
        adminPaymentOrderBasicVo.setPayAmount( paymentOrder.getPayAmount() );
        adminPaymentOrderBasicVo.setCurrency( paymentOrder.getCurrency() );
        adminPaymentOrderBasicVo.setPayChannel( paymentOrder.getPayChannel() );
        adminPaymentOrderBasicVo.setStatus( paymentOrder.getStatus() );
        adminPaymentOrderBasicVo.setThirdTradeNo( paymentOrder.getThirdTradeNo() );
        adminPaymentOrderBasicVo.setExpireTime( paymentOrder.getExpireTime() );
        adminPaymentOrderBasicVo.setPayTime( paymentOrder.getPayTime() );
        adminPaymentOrderBasicVo.setCreateTime( paymentOrder.getCreateTime() );

        return adminPaymentOrderBasicVo;
    }
}
