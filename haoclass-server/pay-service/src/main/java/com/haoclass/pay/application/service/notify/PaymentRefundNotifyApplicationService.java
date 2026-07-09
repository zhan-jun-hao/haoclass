package com.haoclass.pay.application.service.notify;

import java.time.LocalDateTime;

/**
 * 退款回调应用服务。
 */
public interface PaymentRefundNotifyApplicationService {

    /**
     * 处理模拟退款成功回调。
     *
     * @param refundNo      退款单号
     * @param thirdRefundNo 第三方退款流水号
     * @param refundTime    退款成功时间
     */
    void handleMockRefundSuccess(String refundNo, String thirdRefundNo, LocalDateTime refundTime);
}
