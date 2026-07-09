package com.haoclass.pay.interfaces.controller.client;

import com.haoclass.common.result.Result;
import com.haoclass.pay.application.service.client.ClientPaymentOrderApplicationService;
import com.haoclass.pay.interfaces.vo.payment.client.response.ClientPaymentOrderDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * c端-用户支付
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pay/client/payments")
public class ClientOrderPayController {

    private final ClientPaymentOrderApplicationService clientPaymentOrderApplicationService;

    /**
     * 查询当前用户的支付订单详情
     *
     * @param paymentNo 支付单号
     * @return 支付订单详情
     */
    @GetMapping("{paymentNo}")
    public Result<ClientPaymentOrderDetailVo> getPaymentOrderDetail(@PathVariable("paymentNo") String paymentNo) {
        log.info("C端查询支付订单详情, paymentNo: {}", paymentNo);
        return Result.success(clientPaymentOrderApplicationService.getPaymentOrderDetail(paymentNo));
    }

    /**
     * 用户实际支付订单成功
     *
     * @param paymentNo
     * @return
     */
    @PostMapping("{paymentNo}/mock-success")
    public Result<Void> paySuccessOrder(@PathVariable("paymentNo") String paymentNo) {
        log.info("C端用户支付订单, paymentNo: {}", paymentNo);
        clientPaymentOrderApplicationService.mockPaySuccess(paymentNo);
        return Result.success();
    }

    /**
     * 用户订单支付失败
     *
     * @param paymentNo
     * @return
     */
    @PostMapping("{paymentNo}/mock-fail")
    public Result<Void> payFailOrder(@PathVariable("paymentNo") String paymentNo) {
        log.info("C端用户支付订单失败, paymentNo: {}", paymentNo);
        clientPaymentOrderApplicationService.mockPayFail(paymentNo);
        return Result.success();
    }
}
