package com.haoclass.pay.application.converter.client;

import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.client.response.ClientPaymentOrderDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * C端支付订单对象转换器。
 */
@Mapper
public interface ClientPaymentOrderConverter {

    ClientPaymentOrderConverter INSTANCE = Mappers.getMapper(ClientPaymentOrderConverter.class);

    ClientPaymentOrderDetailVo poToDetailVo(PaymentOrder paymentOrder);
}
