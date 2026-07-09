package com.haoclass.pay.infrastructure.config;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付SDK配置
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "haoclass.pay.wechat", name = "enabled", havingValue = "true")
public class WechatPaySdkConfig {

    private final WechatPayProperties properties;

    /**
     * 微信支付SDK核心配置
     */
    @Bean
    public RSAAutoCertificateConfig wechatPayCoreConfig() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(properties.getMchId())
                .privateKeyFromPath(properties.getPrivateKeyPath())
                .merchantSerialNumber(properties.getMerchantSerialNumber())
                .apiV3Key(properties.getApiV3Key())
                .build();
    }

    /**
     * 微信Native支付服务
     */
    @Bean
    public NativePayService nativePayService(RSAAutoCertificateConfig wechatPayCoreConfig) {
        return new NativePayService.Builder()
                .config(wechatPayCoreConfig)
                .build();
    }

    /**
     * 微信支付回调通知解析器
     */
    @Bean
    public NotificationParser notificationParser(RSAAutoCertificateConfig wechatPayCoreConfig) {
        return new NotificationParser(wechatPayCoreConfig);
    }

    /**
     * 微信退款服务
     *
     * @param wechatPayCoreConfig
     * @return
     */
    @Bean
    public RefundService refundService(RSAAutoCertificateConfig wechatPayCoreConfig) {
        return new RefundService.Builder()
                .config(wechatPayCoreConfig)
                .build();
    }
}
