package com.haoclass.pay.application.converter.admin;

import com.haoclass.pay.domain.model.query.PaymentRefundOrderQuery;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import com.haoclass.pay.interfaces.vo.refund.admin.request.AdminPaymentRefundOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderBasicVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理端退款订单对象转换器。
 */
@Mapper
public interface AdminPaymentRefundOrderConverter {

    AdminPaymentRefundOrderConverter INSTANCE = Mappers.getMapper(AdminPaymentRefundOrderConverter.class);

    PaymentRefundOrderQuery reqVoToQuery(AdminPaymentRefundOrderPageQueryReqVo reqVo);

    List<AdminPaymentRefundOrderBasicVo> poToBasicVo(List<PaymentRefundOrder> refundOrders);

    AdminPaymentRefundOrderDetailVo poToDetailVo(PaymentRefundOrder refundOrder);
}
