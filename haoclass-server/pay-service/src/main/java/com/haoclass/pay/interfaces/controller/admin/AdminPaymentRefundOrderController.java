package com.haoclass.pay.interfaces.controller.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.pay.application.service.admin.AdminPaymentRefundOrderApplicationService;
import com.haoclass.pay.interfaces.vo.refund.admin.request.AdminPaymentRefundOrderPageQueryReqVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderBasicVo;
import com.haoclass.pay.interfaces.vo.refund.admin.response.AdminPaymentRefundOrderDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端退款订单管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay/admin/refunds")
public class AdminPaymentRefundOrderController {

    private final AdminPaymentRefundOrderApplicationService adminPaymentRefundOrderApplicationService;

    /**
     * 分页查询退款订单
     *
     * @param reqVo 查询条件
     * @return 退款订单分页结果
     */
    @GetMapping
    public Result<PageResult<AdminPaymentRefundOrderBasicVo>> getRefundOrderPageList(@Validated AdminPaymentRefundOrderPageQueryReqVo reqVo) {
        log.info("管理端分页查询退款订单, reqVo: {}", reqVo);
        return Result.success(adminPaymentRefundOrderApplicationService.getRefundOrderPageList(reqVo));
    }

    /**
     * 查询退款订单详情
     *
     * @param refundNo 退款单号
     * @return 退款订单详情
     */
    @GetMapping("{refundNo}")
    public Result<AdminPaymentRefundOrderDetailVo> getRefundOrderDetail(@PathVariable("refundNo") String refundNo) {
        log.info("管理端查询退款订单详情, refundNo: {}", refundNo);
        return Result.success(adminPaymentRefundOrderApplicationService.getRefundOrderDetail(refundNo));
    }
}
