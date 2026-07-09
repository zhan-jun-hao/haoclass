package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseCommentStatusEnum {

    PENDING_REVIEW(0, "待审核"),

    PUBLISHED(1, "已发布"),

    HIDDEN(2, "隐藏"),

    REJECTED(3, "已驳回");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseCommentStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseCommentStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
