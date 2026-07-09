package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程订单支付方式。
 */
@Getter
@AllArgsConstructor
public enum CourseOrderPayTypeEnum {

    UNPAID(0, "未支付"),

    WECHAT(1, "微信"),

    ALIPAY(2, "支付宝"),

    MOCK(3, "模拟支付");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseOrderPayTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseOrderPayTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
