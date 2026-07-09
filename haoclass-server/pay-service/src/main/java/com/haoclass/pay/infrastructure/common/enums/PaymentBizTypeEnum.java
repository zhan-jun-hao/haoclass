package com.haoclass.pay.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付业务类型
 */
@Getter
@AllArgsConstructor
public enum PaymentBizTypeEnum {

    /**
     * 课程订单
     */
    COURSE_ORDER(1, "课程订单");

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
    public static PaymentBizTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (PaymentBizTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
