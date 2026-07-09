package com.haoclass.pay.application.converter.inner;

import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InnerOrderPaymentConverter {

    InnerOrderPaymentConverter INSTANCE = Mappers.getMapper(InnerOrderPaymentConverter.class);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "payChannel", ignore = true)
    CreatePaymentResponse poToInnerResp(PaymentOrder po);

}
