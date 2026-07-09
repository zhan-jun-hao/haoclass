package com.haoclass.pay.interfaces.controller.notify;

import com.haoclass.pay.application.service.notify.PaymentRefundNotifyApplicationService;
import com.haoclass.pay.interfaces.vo.notify.request.MockRefundNotifyReqVo;
import com.haoclass.pay.interfaces.vo.notify.response.PayNotifyRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟退款回调接口。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay/notify/mock")
public class MockRefundNotifyController {

    private final PaymentRefundNotifyApplicationService paymentRefundNotifyApplicationService;

    /**
     * 模拟退款成功回调。
     *
     * @param reqVo 模拟退款成功回调请求对象
     * @return 支付回调响应
     */
    @PostMapping("/refund")
    public PayNotifyRespVo refundSuccess(@Valid @RequestBody MockRefundNotifyReqVo reqVo) {
        try {
            paymentRefundNotifyApplicationService.handleMockRefundSuccess(
                    reqVo.getRefundNo(),
                    reqVo.getThirdRefundNo(),
                    reqVo.getRefundTime()
            );
            return PayNotifyRespVo.success();
        } catch (Exception e) {
            log.error("模拟退款回调处理失败, refundNo: {}", reqVo.getRefundNo(), e);
            return PayNotifyRespVo.fail();
        }
    }
}
