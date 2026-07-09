package com.haoclass.pay.application.converter.inner;

import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:28+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class InnerOrderPaymentConverterImpl implements InnerOrderPaymentConverter {

    @Override
    public CreatePaymentResponse poToInnerResp(PaymentOrder po) {
        if ( po == null ) {
            return null;
        }

        CreatePaymentResponse createPaymentResponse = new CreatePaymentResponse();

        createPaymentResponse.setPaymentNo( po.getPaymentNo() );
        createPaymentResponse.setPayAmount( po.getPayAmount() );
        createPaymentResponse.setExpireTime( po.getExpireTime() );
        createPaymentResponse.setCodeUrl( po.getCodeUrl() );

        return createPaymentResponse;
    }
}
