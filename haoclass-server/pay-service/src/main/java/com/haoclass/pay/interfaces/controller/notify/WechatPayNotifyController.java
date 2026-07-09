package com.haoclass.pay.interfaces.controller.notify;

import com.haoclass.pay.application.service.notify.WechatPayNotifyApplicationService;
import com.haoclass.pay.interfaces.vo.notify.response.PayNotifyRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信支付回调接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay/notify/wechat")
@ConditionalOnProperty(prefix = "haoclass.pay.wechat", name = "enabled", havingValue = "true")
public class WechatPayNotifyController {

    private static final String HEADER_SERIAL = "Wechatpay-Serial";
    private static final String HEADER_SIGNATURE = "Wechatpay-Signature";
    private static final String HEADER_NONCE = "Wechatpay-Nonce";
    private static final String HEADER_TIMESTAMP = "Wechatpay-Timestamp";

    private final WechatPayNotifyApplicationService wechatPayNotifyApplicationService;

    /**
     * 微信支付成功回调
     *
     * @param serial    微信支付平台证书序列号
     * @param signature 微信回调签名
     * @param nonce     微信回调随机串
     * @param timestamp 微信回调时间戳
     * @param body      微信回调原始报文
     * @return 微信回调响应
     */
    @PostMapping("/payment")
    public PayNotifyRespVo paymentNotify(
            @RequestHeader(HEADER_SERIAL) String serial,
            @RequestHeader(HEADER_SIGNATURE) String signature,
            @RequestHeader(HEADER_NONCE) String nonce,
            @RequestHeader(HEADER_TIMESTAMP) String timestamp,
            @RequestBody String body) {
        try {
            wechatPayNotifyApplicationService.handlePaymentNotify(serial, signature, nonce, timestamp, body);
            return PayNotifyRespVo.success();
        } catch (Exception e) {
            log.error("微信支付回调处理失败", e);
            return PayNotifyRespVo.fail();
        }
    }

    /**
     * 微信退款成功回调
     *
     * @param serial    微信支付平台证书序列号
     * @param signature 微信回调签名
     * @param nonce     微信回调随机串
     * @param timestamp 微信回调时间戳
     * @param body      微信回调原始报文
     * @return 微信回调响应
     */
    @PostMapping("/refund")
    public PayNotifyRespVo refundNotify(
            @RequestHeader(HEADER_SERIAL) String serial,
            @RequestHeader(HEADER_SIGNATURE) String signature,
            @RequestHeader(HEADER_NONCE) String nonce,
            @RequestHeader(HEADER_TIMESTAMP) String timestamp,
            @RequestBody String body) {
        try {
            wechatPayNotifyApplicationService.handleRefundNotify(serial, signature, nonce, timestamp, body);
            return PayNotifyRespVo.success();
        } catch (Exception e) {
            log.error("微信退款回调处理失败", e);
            return PayNotifyRespVo.fail();
        }
    }
}
