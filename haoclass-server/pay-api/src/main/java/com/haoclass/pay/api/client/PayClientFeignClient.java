package com.haoclass.pay.api.client;

import com.haoclass.common.result.Result;
import com.haoclass.pay.api.dto.request.CreatePaymentRequest;
import com.haoclass.pay.api.dto.request.CreateRefundRequest;
import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pay-service", path = "/api/pay/inner/payments")
public interface PayClientFeignClient {

    /**
     * 创建支付订单
     *
     * @param request
     * @return
     */
    @PostMapping
    Result<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request);

    /**
     * 创建退款订单
     *
     * @param request 创建退款单请求
     * @return 创建退款单结果
     */
    @PostMapping("/refunds")
    Result<CreateRefundResponse> createRefund(@RequestBody CreateRefundRequest request);

}
