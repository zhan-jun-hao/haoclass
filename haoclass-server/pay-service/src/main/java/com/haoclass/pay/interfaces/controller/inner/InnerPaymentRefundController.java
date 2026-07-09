package com.haoclass.pay.interfaces.controller.inner;

import com.haoclass.common.result.Result;
import com.haoclass.pay.api.dto.request.CreateRefundRequest;
import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import com.haoclass.pay.application.service.inner.InnerPaymentRefundApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务-退款领域
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pay/inner/payments/refunds")
public class InnerPaymentRefundController {

    private final InnerPaymentRefundApplicationService innerPaymentRefundApplicationService;

    /**
     * 创建退款订单
     *
     * @param request 创建退款订单请求
     * @return 创建退款订单结果
     */
    @PostMapping
    public Result<CreateRefundResponse> createRefund(@RequestBody CreateRefundRequest request) {
        log.info("内部服务创建退款订单, request: {}", request);
        return Result.success(innerPaymentRefundApplicationService.createRefund(request));
    }
}
