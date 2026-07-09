package com.haoclass.pay.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.pay.interfaces.vo.refund.admin.request.AdminPaymentRefundOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderBasicVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderDetailVo;

/**
 * 管理端退款订单应用服务。
 */
public interface AdminPaymentRefundOrderApplicationService {

    /**
     * 分页查询退款订单。
     *
     * @param reqVo 查询条件
     * @return 退款订单分页结果
     */
    PageResult<AdminPaymentRefundOrderBasicVo> getRefundOrderPageList(AdminPaymentRefundOrderPageQueryReqVo reqVo);

    /**
     * 查询退款订单详情。
     *
     * @param refundNo 退款单号
     * @return 退款订单详情
     */
    AdminPaymentRefundOrderDetailVo getRefundOrderDetail(String refundNo);
}
