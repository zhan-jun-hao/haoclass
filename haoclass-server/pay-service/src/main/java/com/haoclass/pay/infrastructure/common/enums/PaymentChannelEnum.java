package com.haoclass.pay.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道。
 */
@Getter
@AllArgsConstructor
public enum PaymentChannelEnum {

    /**
     * 微信支付。
     */
    WECHAT(1, "微信支付"),

    /**
     * 支付宝支付。
     */
    ALIPAY(2, "支付宝支付"),

    /**
     * 模拟支付。
     */
    MOCK(3, "模拟支付");

    /**
     * 渠道编码。
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 渠道描述。
     */
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PaymentChannelEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (PaymentChannelEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
