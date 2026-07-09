package com.haoclass.coupon.infrastructure.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券模板状态。
 */
@Getter
@AllArgsConstructor
public enum CouponTemplateStatusEnum {

    /**
     * 草稿。
     */
    DRAFT(0, "草稿"),

    /**
     * 已发布。
     */
    PUBLISHED(1, "已发布"),

    /**
     * 已停止。
     */
    STOPPED(2, "已停止");

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
    public static CouponTemplateStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }

        for (CouponTemplateStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return null;
    }
}
