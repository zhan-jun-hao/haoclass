package com.haoclass.pay.application.converter.admin;

import com.haoclass.pay.domain.model.query.PaymentOrderQuery;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.admin.request.AdminPaymentOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderBasicVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理端支付订单对象转换器。
 */
@Mapper
public interface AdminPaymentOrderConverter {

    AdminPaymentOrderConverter INSTANCE = Mappers.getMapper(AdminPaymentOrderConverter.class);

    PaymentOrderQuery reqVoToQuery(AdminPaymentOrderPageQueryReqVo reqVo);

    List<AdminPaymentOrderBasicVo> poToBasicVo(List<PaymentOrder> paymentOrders);

    AdminPaymentOrderDetailVo poToDetailVo(PaymentOrder paymentOrder);
}
