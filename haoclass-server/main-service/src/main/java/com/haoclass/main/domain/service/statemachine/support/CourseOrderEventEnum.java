package com.haoclass.main.domain.service.statemachine.support;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程订单事件
 */
@Getter
@AllArgsConstructor
public enum CourseOrderEventEnum {

    USER_CANCEL(0, "用户取消"),

    SYSTEM_CANCEL(1, "系统取消"),

    TIMEOUT_CLOSE(2, "超时关闭"),

    PAYMENT_SUCCESS(3, "支付成功"),

    REFUND_SUCCESS(4, "退款成功"),;

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseOrderEventEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseOrderEventEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
