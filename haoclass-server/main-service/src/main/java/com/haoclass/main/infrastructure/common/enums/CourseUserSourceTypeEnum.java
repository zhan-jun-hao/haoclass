package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 课程权益来源。
 */
@Getter
@AllArgsConstructor
public enum CourseUserSourceTypeEnum {

    PURCHASE(0, "购买"),

    ADMIN_GRANT(1, "后台赠送"),

    FREE_CLAIM(2, "免费领取");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseUserSourceTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseUserSourceTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
