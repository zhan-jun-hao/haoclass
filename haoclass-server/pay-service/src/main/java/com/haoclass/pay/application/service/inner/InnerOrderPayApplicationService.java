package com.haoclass.pay.application.service.inner;

import com.haoclass.pay.api.dto.request.CreatePaymentRequest;
import com.haoclass.pay.api.dto.response.CreatePaymentResponse;

public interface InnerOrderPayApplicationService {

    /**
     * 创建支付订单
     */
    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * 批量关闭超时支付订单
     */
    void closeExpiredPendingOrders(int batchSize);
}
