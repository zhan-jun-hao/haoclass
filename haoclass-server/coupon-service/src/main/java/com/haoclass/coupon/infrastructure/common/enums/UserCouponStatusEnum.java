package com.haoclass.coupon.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户优惠券状态。
 */
@Getter
@AllArgsConstructor
public enum UserCouponStatusEnum {

    /**
     * 未使用。
     */
    UNUSED(0, "未使用"),

    /**
     * 已被待支付订单锁定。
     */
    LOCKED(1, "已锁定"),

    /**
     * 已使用。
     */
    USED(2, "已使用"),

    /**
     * 已过期。
     */
    EXPIRED(3, "已过期");

    /**
     * 状态码。
     */
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * 状态描述。
     */
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UserCouponStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (UserCouponStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
