package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseCommentLikeStatusEnum {

    CANCELLED(0, "已取消"),

    LIKED(1, "已点赞");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseCommentLikeStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseCommentLikeStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
