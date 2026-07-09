package com.haoclass.pay.interfaces.vo.notify.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 支付回调响应对象。
 */
@Data
@AllArgsConstructor
public class PayNotifyRespVo {

    /**
     * 响应编码。
     */
    private String code;

    /**
     * 响应消息。
     */
    private String message;

    /**
     * 处理成功。
     */
    public static PayNotifyRespVo success() {
        return new PayNotifyRespVo("SUCCESS", "成功");
    }

    /**
     * 处理失败。
     */
    public static PayNotifyRespVo fail() {
        return new PayNotifyRespVo("FAIL", "失败");
    }
}
