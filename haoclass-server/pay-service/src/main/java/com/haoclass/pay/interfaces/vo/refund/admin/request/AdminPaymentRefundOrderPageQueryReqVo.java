package com.haoclass.pay.interfaces.vo.refund.admin.request;

import com.haoclass.common.query.TimeRangePageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 管理端退款订单分页查询请求对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminPaymentRefundOrderPageQueryReqVo extends TimeRangePageQuery {

    /**
     * 退款单号。
     */
    private String refundNo;

    /**
     * 原支付单号。
     */
    private String paymentNo;

    /**
     * 业务类型：1课程订单。
     */
    @Min(value = 1, message = "业务类型不正确")
    @Max(value = 1, message = "业务类型不正确")
    private Integer bizType;

    /**
     * 业务订单号。
     */
    private String bizOrderNo;

    /**
     * 退款用户ID。
     */
    private Long userId;

    /**
     * 支付渠道：1微信，2支付宝，3模拟支付。
     */
    @Min(value = 1, message = "支付渠道不正确")
    @Max(value = 3, message = "支付渠道不正确")
    private Integer payChannel;

    /**
     * 退款状态：0待退款，1退款中，2退款成功，3退款失败，4已关闭。
     */
    @Min(value = 0, message = "退款状态不正确")
    @Max(value = 4, message = "退款状态不正确")
    private Integer status;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

}
