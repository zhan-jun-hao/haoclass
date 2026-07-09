package com.haoclass.pay.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.pay.application.converter.admin.AdminPaymentOrderConverter;
import com.haoclass.pay.application.service.admin.AdminPaymentOrderApplicationService;
import com.haoclass.pay.domain.model.query.PaymentOrderQuery;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.interfaces.vo.payment.admin.request.AdminPaymentOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderBasicVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端支付订单应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class AdminPaymentOrderApplicationServiceImpl implements AdminPaymentOrderApplicationService {

    private final PaymentOrderService paymentOrderService;

    @Override
    public PageResult<AdminPaymentOrderBasicVo> getPaymentOrderPageList(AdminPaymentOrderPageQueryReqVo reqVo) {
        PaymentOrderQuery query = AdminPaymentOrderConverter.INSTANCE.reqVoToQuery(reqVo);
        IPage<PaymentOrder> page = paymentOrderService.pageQuery(query);
        return PageResult.success(page, AdminPaymentOrderConverter.INSTANCE.poToBasicVo(page.getRecords()));
    }

    @Override
    public AdminPaymentOrderDetailVo getPaymentOrderDetail(String paymentNo) {
        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderByPaymentNo(paymentNo);
        return AdminPaymentOrderConverter.INSTANCE.poToDetailVo(paymentOrder);
    }
}
