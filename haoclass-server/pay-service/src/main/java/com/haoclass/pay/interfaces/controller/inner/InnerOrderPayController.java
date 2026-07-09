package com.haoclass.pay.interfaces.controller.inner;

import com.haoclass.common.result.Result;
import com.haoclass.pay.api.dto.request.CreatePaymentRequest;
import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.application.service.inner.InnerOrderPayApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务-支付领域
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pay/inner/payments")
public class InnerOrderPayController {

    private final InnerOrderPayApplicationService innerOrderPayApplicationService;

    /**
     * 创建支付订单
     *
     */
    @PostMapping
    Result<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        return Result.success(innerOrderPayApplicationService.createPayment(request));
    }

}
