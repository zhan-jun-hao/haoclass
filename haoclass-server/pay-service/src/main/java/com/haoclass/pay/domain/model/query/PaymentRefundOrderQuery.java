package com.haoclass.pay.domain.model.query;

import com.haoclass.common.query.TimeRangePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付退款订单分页查询条件。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentRefundOrderQuery extends TimeRangePageQuery {

    /**
     * 退款单号。
     */
    private String refundNo;

    /**
     * 原支付单号。
     */
    private String paymentNo;

    /**
     * 业务类型。
     */
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
     * 支付渠道。
     */
    private Integer payChannel;

    /**
     * 退款状态。
     */
    private Integer status;

    /**
     * 第三方退款流水号。
     */
    private String thirdRefundNo;

}
