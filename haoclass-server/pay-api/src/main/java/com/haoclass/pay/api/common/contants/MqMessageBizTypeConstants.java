package com.haoclass.pay.api.common.contants;

/**
 * 支付服务-mq业务类型
 */
public interface MqMessageBizTypeConstants {

    /**
     * 支付成功
     */
    String PAYMENT_SUCCESS = "PAYMENT_SUCCESS";

    /**
     * 支付失败
     */
    String PAYMENT_FAILED = "PAYMENT_FAILED";

    /**
     * 支付超时
     */
    String PAYMENT_EXPIRED = "PAYMENT_EXPIRED";

    /**
     * 退款成功
     */
    String REFUND_SUCCESS = "REFUND_SUCCESS";

}
