package com.haoclass.pay.application.converter.inner;

import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 内部退款订单对象转换器。
 */
@Mapper
public interface InnerPaymentRefundOrderConverter {

    InnerPaymentRefundOrderConverter INSTANCE = Mappers.getMapper(InnerPaymentRefundOrderConverter.class);

    @Mapping(target = "status", ignore = true)
    CreateRefundResponse poToInnerResp(PaymentRefundOrder refundOrder);

    @Mapping(target = "refundNo", ignore = true)
    @Mapping(target = "refundAmount", ignore = true)
    @Mapping(target = "thirdRefundNo", ignore = true)
    @Mapping(target = "refundReason", ignore = true)
    @Mapping(target = "applyTime", ignore = true)
    @Mapping(target = "refundTime", ignore = true)
    PaymentRefundOrder orderPoToRefundPo(PaymentOrder order);
}
