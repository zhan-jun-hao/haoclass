package com.haoclass.pay.application.converter.inner;

import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:28+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class InnerPaymentRefundOrderConverterImpl implements InnerPaymentRefundOrderConverter {

    @Override
    public CreateRefundResponse poToInnerResp(PaymentRefundOrder refundOrder) {
        if ( refundOrder == null ) {
            return null;
        }

        CreateRefundResponse createRefundResponse = new CreateRefundResponse();

        createRefundResponse.setRefundNo( refundOrder.getRefundNo() );
        createRefundResponse.setPaymentNo( refundOrder.getPaymentNo() );
        createRefundResponse.setRefundAmount( refundOrder.getRefundAmount() );

        return createRefundResponse;
    }

    @Override
    public PaymentRefundOrder orderPoToRefundPo(PaymentOrder order) {
        if ( order == null ) {
            return null;
        }

        PaymentRefundOrder paymentRefundOrder = new PaymentRefundOrder();

        paymentRefundOrder.setId( order.getId() );
        paymentRefundOrder.setCreateTime( order.getCreateTime() );
        paymentRefundOrder.setUpdateTime( order.getUpdateTime() );
        paymentRefundOrder.setCreatedUser( order.getCreatedUser() );
        paymentRefundOrder.setUpdatedUser( order.getUpdatedUser() );
        paymentRefundOrder.setDeleted( order.getDeleted() );
        paymentRefundOrder.setPaymentNo( order.getPaymentNo() );
        paymentRefundOrder.setBizType( order.getBizType() );
        paymentRefundOrder.setBizOrderId( order.getBizOrderId() );
        paymentRefundOrder.setBizOrderNo( order.getBizOrderNo() );
        paymentRefundOrder.setUserId( order.getUserId() );
        paymentRefundOrder.setPayAmount( order.getPayAmount() );
        paymentRefundOrder.setCurrency( order.getCurrency() );
        paymentRefundOrder.setPayChannel( order.getPayChannel() );
        paymentRefundOrder.setStatus( paymentStatusEnumToPaymentRefundStatusEnum( order.getStatus() ) );
        paymentRefundOrder.setThirdTradeNo( order.getThirdTradeNo() );
        paymentRefundOrder.setFailureReason( order.getFailureReason() );
        paymentRefundOrder.setCloseTime( order.getCloseTime() );

        return paymentRefundOrder;
    }

    protected PaymentRefundStatusEnum paymentStatusEnumToPaymentRefundStatusEnum(PaymentStatusEnum paymentStatusEnum) {
        if ( paymentStatusEnum == null ) {
            return null;
        }

        PaymentRefundStatusEnum paymentRefundStatusEnum;

        switch ( paymentStatusEnum ) {
            case PENDING: paymentRefundStatusEnum = PaymentRefundStatusEnum.PENDING;
            break;
            case SUCCESS: paymentRefundStatusEnum = PaymentRefundStatusEnum.SUCCESS;
            break;
            case CLOSED: paymentRefundStatusEnum = PaymentRefundStatusEnum.CLOSED;
            break;
            case FAILED: paymentRefundStatusEnum = PaymentRefundStatusEnum.FAILED;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + paymentStatusEnum );
        }

        return paymentRefundStatusEnum;
    }
}
