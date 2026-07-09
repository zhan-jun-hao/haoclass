package com.haoclass.pay.interfaces.vo.payment.admin.request;

import com.haoclass.common.query.TimeRangePageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 管理端支付订单分页查询请求对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminPaymentOrderPageQueryReqVo extends TimeRangePageQuery {

    /**
     * 支付单号。
     */
    private String paymentNo;

    /**
     * 业务类型：1课程订单
     */
    @Min(value = 1, message = "业务类型不正确")
    @Max(value = 1, message = "业务类型不正确")
    private Integer bizType;

    /**
     * 业务订单号。
     */
    private String bizOrderNo;

    /**
     * 支付用户ID。
     */
    private Long userId;

    /**
     * 支付渠道：1微信，2支付宝，3模拟支付。
     */
    @Min(value = 1, message = "支付渠道不正确")
    @Max(value = 3, message = "支付渠道不正确")
    private Integer payChannel;

    /**
     * 支付状态：0待支付，1支付成功，2已关闭，3支付失败。
     */
    @Min(value = 0, message = "支付状态不正确")
    @Max(value = 3, message = "支付状态不正确")
    private Integer status;

    /**
     * 第三方支付流水号。
     */
    private String thirdTradeNo;

}
