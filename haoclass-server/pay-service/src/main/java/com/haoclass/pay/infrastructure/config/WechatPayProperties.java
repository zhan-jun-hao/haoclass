package com.haoclass.pay.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "haoclass.pay.wechat")
public class WechatPayProperties {

    /**
     * 是否启用微信支付
     */
    private Boolean enabled = false;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户私钥路径
     */
    private String privateKeyPath;

    /**
     * APIv3密钥
     */
    private String apiV3Key;

    /**
     * 支付结果通知地址
     */
    private String notifyUrl;

    /**
     * 退款结果通知地址
     */
    private String refundNotifyUrl;
}
