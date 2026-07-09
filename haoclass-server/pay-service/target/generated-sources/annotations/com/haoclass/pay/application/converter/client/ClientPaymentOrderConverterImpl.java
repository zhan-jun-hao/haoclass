package com.haoclass.pay.application.converter.client;

import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.client.response.ClientPaymentOrderDetailVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:28+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientPaymentOrderConverterImpl implements ClientPaymentOrderConverter {

    @Override
    public ClientPaymentOrderDetailVo poToDetailVo(PaymentOrder paymentOrder) {
        if ( paymentOrder == null ) {
            return null;
        }

        ClientPaymentOrderDetailVo clientPaymentOrderDetailVo = new ClientPaymentOrderDetailVo();

        clientPaymentOrderDetailVo.setPaymentNo( paymentOrder.getPaymentNo() );
        clientPaymentOrderDetailVo.setBizType( paymentOrder.getBizType() );
        clientPaymentOrderDetailVo.setBizOrderNo( paymentOrder.getBizOrderNo() );
        clientPaymentOrderDetailVo.setSubject( paymentOrder.getSubject() );
        clientPaymentOrderDetailVo.setPayAmount( paymentOrder.getPayAmount() );
        clientPaymentOrderDetailVo.setCurrency( paymentOrder.getCurrency() );
        clientPaymentOrderDetailVo.setPayChannel( paymentOrder.getPayChannel() );
        clientPaymentOrderDetailVo.setStatus( paymentOrder.getStatus() );
        clientPaymentOrderDetailVo.setExpireTime( paymentOrder.getExpireTime() );
        clientPaymentOrderDetailVo.setPayTime( paymentOrder.getPayTime() );
        clientPaymentOrderDetailVo.setCloseTime( paymentOrder.getCloseTime() );
        clientPaymentOrderDetailVo.setCreateTime( paymentOrder.getCreateTime() );

        return clientPaymentOrderDetailVo;
    }
}
