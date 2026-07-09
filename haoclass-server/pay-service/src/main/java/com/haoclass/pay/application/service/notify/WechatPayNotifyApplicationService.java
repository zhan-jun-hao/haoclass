package com.haoclass.pay.application.service.notify;

/**
 * 微信支付回调应用服务
 */
public interface WechatPayNotifyApplicationService {

    /**
     * 处理微信支付成功回调
     *
     * @param serial    微信支付平台证书序列号
     * @param signature 微信回调签名
     * @param nonce     微信回调随机串
     * @param timestamp 微信回调时间戳
     * @param body      微信回调原始报文
     */
    void handlePaymentNotify(String serial, String signature, String nonce, String timestamp, String body);

    /**
     * 处理微信退款成功回调
     *
     * @param serial    微信支付平台证书序列号
     * @param signature 微信回调签名
     * @param nonce     微信回调随机串
     * @param timestamp 微信回调时间戳
     * @param body      微信回调原始报文
     */
    void handleRefundNotify(String serial, String signature, String nonce, String timestamp, String body);
}
