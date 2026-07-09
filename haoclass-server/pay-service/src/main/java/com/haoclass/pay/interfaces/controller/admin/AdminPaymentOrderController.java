package com.haoclass.pay.interfaces.controller.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.pay.application.service.admin.AdminPaymentOrderApplicationService;
import com.haoclass.pay.interfaces.vo.payment.admin.request.AdminPaymentOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderBasicVo;
import com.haoclass.pay.interfaces.vo.payment.admin.response.AdminPaymentOrderDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端支付订单管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay/admin/payments")
public class AdminPaymentOrderController {

    private final AdminPaymentOrderApplicationService adminPaymentOrderApplicationService;

    /**
     * 分页查询支付订单
     *
     * @param reqVo 查询条件
     * @return 支付订单分页结果
     */
    @GetMapping
    public Result<PageResult<AdminPaymentOrderBasicVo>> getPaymentOrderPageList(@Validated AdminPaymentOrderPageQueryReqVo reqVo) {
        log.info("管理端分页查询支付订单, reqVo: {}", reqVo);
        return Result.success(adminPaymentOrderApplicationService.getPaymentOrderPageList(reqVo));
    }

    /**
     * 查询支付订单详情
     *
     * @param paymentNo 支付单号
     * @return 支付订单详情
     */
    @GetMapping("{paymentNo}")
    public Result<AdminPaymentOrderDetailVo> getPaymentOrderDetail(@PathVariable("paymentNo") String paymentNo) {
        log.info("管理端查询支付订单详情, paymentNo: {}", paymentNo);
        return Result.success(adminPaymentOrderApplicationService.getPaymentOrderDetail(paymentNo));
    }
}
