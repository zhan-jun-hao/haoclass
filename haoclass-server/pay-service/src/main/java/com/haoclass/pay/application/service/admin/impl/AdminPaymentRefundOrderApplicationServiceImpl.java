package com.haoclass.pay.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.pay.application.converter.admin.AdminPaymentRefundOrderConverter;
import com.haoclass.pay.application.service.admin.AdminPaymentRefundOrderApplicationService;
import com.haoclass.pay.domain.model.query.PaymentRefundOrderQuery;
import com.haoclass.pay.domain.service.PaymentRefundOrderService;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import com.haoclass.pay.interfaces.vo.refund.admin.request.AdminPaymentRefundOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderBasicVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 管理端退款订单应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminPaymentRefundOrderApplicationServiceImpl implements AdminPaymentRefundOrderApplicationService {

    private final PaymentRefundOrderService paymentRefundOrderService;

    @Override
    public PageResult<AdminPaymentRefundOrderBasicVo> getRefundOrderPageList(AdminPaymentRefundOrderPageQueryReqVo reqVo) {
        PaymentRefundOrderQuery query = AdminPaymentRefundOrderConverter.INSTANCE.reqVoToQuery(reqVo);
        IPage<PaymentRefundOrder> page = paymentRefundOrderService.pageQuery(query);
        return PageResult.success(page, AdminPaymentRefundOrderConverter.INSTANCE.poToBasicVo(page.getRecords()));
    }

    @Override
    public AdminPaymentRefundOrderDetailVo getRefundOrderDetail(String refundNo) {
        PaymentRefundOrder refundOrder = paymentRefundOrderService.getPaymentRefundOrderByRefundNo(refundNo);
        return AdminPaymentRefundOrderConverter.INSTANCE.poToDetailVo(refundOrder);
    }
}
