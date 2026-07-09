package com.haoclass.pay.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 本地消息表状态枚举
 */
@Getter
@AllArgsConstructor
public enum MqMessageStatusEnum {

    /**
     * 待发送
     */
    PENDING_SEND(0, "待发送"),

    /**
     * 已发送
     */
    SUCCESS_SEND(1, "已发送"),

    /**
     * 发送失败
     */
    FAILED_SEND(2, "发送失败")

    ;

    /**
     * 业务类型编码
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 业务类型描述
     */
    private final String desc;

    /**
     * 反序列化为value
     *
     * @param code
     * @return
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MqMessageStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (MqMessageStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}

