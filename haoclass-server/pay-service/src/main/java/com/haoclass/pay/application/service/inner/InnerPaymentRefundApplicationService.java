package com.haoclass.pay.application.service.inner;

import com.haoclass.pay.api.dto.request.CreateRefundRequest;
import com.haoclass.pay.api.dto.response.CreateRefundResponse;

/**
 * 内部服务-退款应用服务。
 */
public interface InnerPaymentRefundApplicationService {

    /**
     * 创建退款订单。
     *
     * @param request 创建退款订单请求
     * @return 创建退款订单结果
     */
    CreateRefundResponse createRefund(CreateRefundRequest request);
}
