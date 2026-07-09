package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程支付类型
 */
@Getter
@AllArgsConstructor
public enum CourseChargeTypeEnum {

    FREE(0, "免费"),

    PAY(1, "付费"),

    VIP_FREE(2, "VIP免费 且 非VIP可以单独购买");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseChargeTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseChargeTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
