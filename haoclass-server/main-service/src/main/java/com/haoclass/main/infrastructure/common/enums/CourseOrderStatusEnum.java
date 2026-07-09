package com.haoclass.main.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseOrderStatusEnum {

    /**
     * 待支付
     */
    PENDING_PAYMENT(0, "待支付"),

    /**
     * 已支付
     */
    PAID(1, "已支付"),

    /**
     * 已取消
     */
    CANCELLED(2, "已取消"),

    /**
     * 已退款
     */
    REFUNDED(3, "已退款"),

    /**
     * 已关闭
     */
    CLOSED(4, "已关闭");

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
    public static CourseOrderStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CourseOrderStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
