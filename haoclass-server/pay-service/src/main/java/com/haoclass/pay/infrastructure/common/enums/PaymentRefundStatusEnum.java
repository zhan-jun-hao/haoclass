package com.haoclass.pay.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付退款状态。
 */
@Getter
@AllArgsConstructor
public enum PaymentRefundStatusEnum {

    /**
     * 待退款。
     */
    PENDING(0, "待退款"),

    /**
     * 退款中。
     */
    PROCESSING(1, "退款中"),

    /**
     * 退款成功。
     */
    SUCCESS(2, "退款成功"),

    /**
     * 退款失败。
     */
    FAILED(3, "退款失败"),

    /**
     * 已关闭。
     */
    CLOSED(4, "已关闭");

    /**
     * 状态码。
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 状态描述。
     */
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PaymentRefundStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (PaymentRefundStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
