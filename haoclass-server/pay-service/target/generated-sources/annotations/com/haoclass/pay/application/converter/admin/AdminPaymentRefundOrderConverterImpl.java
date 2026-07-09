package com.haoclass.pay.application.converter.admin;

import com.haoclass.pay.domain.model.query.PaymentRefundOrderQuery;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import com.haoclass.pay.interfaces.vo.refund.admin.request.AdminPaymentRefundOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderBasicVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:28+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminPaymentRefundOrderConverterImpl implements AdminPaymentRefundOrderConverter {

    @Override
    public PaymentRefundOrderQuery reqVoToQuery(AdminPaymentRefundOrderPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        PaymentRefundOrderQuery paymentRefundOrderQuery = new PaymentRefundOrderQuery();

        paymentRefundOrderQuery.setCurrent( reqVo.getCurrent() );
        paymentRefundOrderQuery.setSize( reqVo.getSize() );
        paymentRefundOrderQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        paymentRefundOrderQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );
        paymentRefundOrderQuery.setRefundNo( reqVo.getRefundNo() );
        paymentRefundOrderQuery.setPaymentNo( reqVo.getPaymentNo() );
        paymentRefundOrderQuery.setBizType( reqVo.getBizType() );
        paymentRefundOrderQuery.setBizOrderNo( reqVo.getBizOrderNo() );
        paymentRefundOrderQuery.setUserId( reqVo.getUserId() );
        paymentRefundOrderQuery.setPayChannel( reqVo.getPayChannel() );
        paymentRefundOrderQuery.setStatus( reqVo.getStatus() );
        paymentRefundOrderQuery.setThirdRefundNo( reqVo.getThirdRefundNo() );

        return paymentRefundOrderQuery;
    }

    @Override
    public List<AdminPaymentRefundOrderBasicVo> poToBasicVo(List<PaymentRefundOrder> refundOrders) {
        if ( refundOrders == null ) {
            return null;
        }

        List<AdminPaymentRefundOrderBasicVo> list = new ArrayList<AdminPaymentRefundOrderBasicVo>( refundOrders.size() );
        for ( PaymentRefundOrder paymentRefundOrder : refundOrders ) {
            list.add( paymentRefundOrderToAdminPaymentRefundOrderBasicVo( paymentRefundOrder ) );
        }

        return list;
    }

    @Override
    public AdminPaymentRefundOrderDetailVo poToDetailVo(PaymentRefundOrder refundOrder) {
        if ( refundOrder == null ) {
            return null;
        }

        AdminPaymentRefundOrderDetailVo adminPaymentRefundOrderDetailVo = new AdminPaymentRefundOrderDetailVo();

        adminPaymentRefundOrderDetailVo.setId( refundOrder.getId() );
        adminPaymentRefundOrderDetailVo.setRefundNo( refundOrder.getRefundNo() );
        adminPaymentRefundOrderDetailVo.setPaymentNo( refundOrder.getPaymentNo() );
        adminPaymentRefundOrderDetailVo.setBizType( refundOrder.getBizType() );
        adminPaymentRefundOrderDetailVo.setBizOrderId( refundOrder.getBizOrderId() );
        adminPaymentRefundOrderDetailVo.setBizOrderNo( refundOrder.getBizOrderNo() );
        adminPaymentRefundOrderDetailVo.setUserId( refundOrder.getUserId() );
        adminPaymentRefundOrderDetailVo.setPayAmount( refundOrder.getPayAmount() );
        adminPaymentRefundOrderDetailVo.setRefundAmount( refundOrder.getRefundAmount() );
        adminPaymentRefundOrderDetailVo.setCurrency( refundOrder.getCurrency() );
        adminPaymentRefundOrderDetailVo.setPayChannel( refundOrder.getPayChannel() );
        adminPaymentRefundOrderDetailVo.setStatus( refundOrder.getStatus() );
        adminPaymentRefundOrderDetailVo.setThirdTradeNo( refundOrder.getThirdTradeNo() );
        adminPaymentRefundOrderDetailVo.setThirdRefundNo( refundOrder.getThirdRefundNo() );
        adminPaymentRefundOrderDetailVo.setRefundReason( refundOrder.getRefundReason() );
        adminPaymentRefundOrderDetailVo.setFailureReason( refundOrder.getFailureReason() );
        adminPaymentRefundOrderDetailVo.setApplyTime( refundOrder.getApplyTime() );
        adminPaymentRefundOrderDetailVo.setRefundTime( refundOrder.getRefundTime() );
        adminPaymentRefundOrderDetailVo.setCloseTime( refundOrder.getCloseTime() );
        adminPaymentRefundOrderDetailVo.setCreateTime( refundOrder.getCreateTime() );
        adminPaymentRefundOrderDetailVo.setUpdateTime( refundOrder.getUpdateTime() );

        return adminPaymentRefundOrderDetailVo;
    }

    protected AdminPaymentRefundOrderBasicVo paymentRefundOrderToAdminPaymentRefundOrderBasicVo(PaymentRefundOrder paymentRefundOrder) {
        if ( paymentRefundOrder == null ) {
            return null;
        }

        AdminPaymentRefundOrderBasicVo adminPaymentRefundOrderBasicVo = new AdminPaymentRefundOrderBasicVo();

        adminPaymentRefundOrderBasicVo.setId( paymentRefundOrder.getId() );
        adminPaymentRefundOrderBasicVo.setRefundNo( paymentRefundOrder.getRefundNo() );
        adminPaymentRefundOrderBasicVo.setPaymentNo( paymentRefundOrder.getPaymentNo() );
        adminPaymentRefundOrderBasicVo.setBizType( paymentRefundOrder.getBizType() );
        adminPaymentRefundOrderBasicVo.setBizOrderNo( paymentRefundOrder.getBizOrderNo() );
        adminPaymentRefundOrderBasicVo.setUserId( paymentRefundOrder.getUserId() );
        adminPaymentRefundOrderBasicVo.setPayAmount( paymentRefundOrder.getPayAmount() );
        adminPaymentRefundOrderBasicVo.setRefundAmount( paymentRefundOrder.getRefundAmount() );
        adminPaymentRefundOrderBasicVo.setPayChannel( paymentRefundOrder.getPayChannel() );
        adminPaymentRefundOrderBasicVo.setStatus( paymentRefundOrder.getStatus() );
        adminPaymentRefundOrderBasicVo.setThirdRefundNo( paymentRefundOrder.getThirdRefundNo() );
        adminPaymentRefundOrderBasicVo.setApplyTime( paymentRefundOrder.getApplyTime() );
        adminPaymentRefundOrderBasicVo.setRefundTime( paymentRefundOrder.getRefundTime() );
        adminPaymentRefundOrderBasicVo.setCreateTime( paymentRefundOrder.getCreateTime() );

        return adminPaymentRefundOrderBasicVo;
    }
}
