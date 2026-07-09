package com.haoclass.pay.interfaces.vo.notify.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模拟退款成功回调请求对象。
 */
@Data
public class MockRefundNotifyReqVo {

    /**
     * 退款单号。
     */
    @NotBlank(message = "退款单号不能为空")
    private String refundNo;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

    /**
     * 退款成功时间。
     */
    private LocalDateTime refundTime;
}
