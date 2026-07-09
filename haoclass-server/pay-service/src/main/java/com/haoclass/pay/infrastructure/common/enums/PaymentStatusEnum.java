package com.haoclass.pay.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态
 */
@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    /**
     * 待支付
     */
    PENDING(0, "待支付"),

    /**
     * 支付成功
     */
    SUCCESS(1, "支付成功"),

    /**
     * 已关闭
     */
    CLOSED(2, "已关闭"),

    /**
     * 支付失败
     */
    FAILED(3, "支付失败");

    /**
     * 状态码
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 状态描述。
     */
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PaymentStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (PaymentStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
