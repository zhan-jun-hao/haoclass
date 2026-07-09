package com.haoclass.main.infrastructure.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseUserStatusEnum {

    /**
     * 失效
     */
    INVALID(0, "失效"),

    /**
     * 有效
     */
    VALID(1, "有效"),

    /**
     * 退款失效
     */
    REFUND_INVALID(2, "退款失效");

    /**
     * 状态码
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseUserStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseUserStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
