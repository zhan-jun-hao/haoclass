package com.haoclass.pay.application.service.client;

import com.haoclass.pay.interfaces.vo.payment.client.response.ClientPaymentOrderDetailVo;

/**
 * C端支付订单应用服务
 */
public interface ClientPaymentOrderApplicationService {

    /**
     * 查询当前用户的支付订单详情
     *
     * @param paymentNo 支付单号
     * @return 支付订单详情
     */
    ClientPaymentOrderDetailVo getPaymentOrderDetail(String paymentNo);

    /**
     * 用户支付成功
     *
     * @param paymentNo
     */
    void mockPaySuccess(String paymentNo);

    /**
     * 用户支付失败
     *
     * @param paymentNo
     */
    void mockPayFail(String paymentNo);
}
