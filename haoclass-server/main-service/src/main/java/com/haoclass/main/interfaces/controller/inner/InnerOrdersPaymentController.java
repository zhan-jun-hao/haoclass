package com.haoclass.main.interfaces.controller.inner;

import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.inner.InnerCourseOrderApplicationService;
import com.haoclass.pay.api.dto.request.PaymentSuccessRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务-订单支付成功履约
 */
@Slf4j
@RestController
@RequestMapping("/api/main/inner/orders/payment")
@RequiredArgsConstructor
public class InnerOrdersPaymentController {

    private final InnerCourseOrderApplicationService innerCourseOrderApplicationService;

    /**
     * 用户成功支付履约
     *
     * @param request 支付成功通知
     * @return 无返回数据
     */
    @PostMapping("/success")
    public Result<Void> paymentSuccess(@RequestBody PaymentSuccessRequest request) {
        log.info("接收支付成功履约通知, paymentNo: {}, bizOrderId: {}", request.getPaymentNo(), request.getBizOrderId());
        innerCourseOrderApplicationService.handlePaymentSuccess(request);
        return Result.success();
    }
}
