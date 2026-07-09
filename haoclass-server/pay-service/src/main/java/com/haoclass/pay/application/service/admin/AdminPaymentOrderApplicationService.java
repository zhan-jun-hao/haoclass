package com.haoclass.pay.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.pay.interfaces.vo.payment.admin.request.AdminPaymentOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderBasicVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderDetailVo;

/**
 * 管理端支付订单应用服务
 */
public interface AdminPaymentOrderApplicationService {

    /**
     * 分页查询支付订单
     *
     * @param reqVo 查询条件
     * @return 支付订单分页结果
     */
    PageResult<AdminPaymentOrderBasicVo> getPaymentOrderPageList(AdminPaymentOrderPageQueryReqVo reqVo);

    /**
     * 查询支付订单详情
     *
     * @param paymentNo 支付单号
     * @return 支付订单详情
     */
    AdminPaymentOrderDetailVo getPaymentOrderDetail(String paymentNo);
}
