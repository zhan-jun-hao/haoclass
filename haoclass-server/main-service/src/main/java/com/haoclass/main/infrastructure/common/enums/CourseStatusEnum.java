package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseStatusEnum {

    DRAFT(0, "草稿"),

    PUBLISHED(1, "上架"),

    UNPUBLISHED(2, "下架");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    /**
     * 根据code获取枚举
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }

}