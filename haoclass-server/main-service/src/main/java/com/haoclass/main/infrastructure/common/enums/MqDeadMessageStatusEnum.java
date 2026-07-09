package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MqDeadMessageStatusEnum {

    /**
     * 待处理
     */
    PENDING_PROCESS(0, "待处理"),

    /**
     * 已重投
     */
    RESEND_SUCCESS(1, "已重投"),

    /**
     * 忽略
     */
    IGNORE(2, "忽略"),

    /**
     * 重投失败
     */
    RESEND_FAIL(3, "重投失败");

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
    public static MqDeadMessageStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for(MqDeadMessageStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
