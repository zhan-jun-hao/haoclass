package com.haoclass.pay.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.pay.domain.model.query.PaymentRefundOrderQuery;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;

import java.time.LocalDateTime;

/**
 * 支付退款订单领域服务。
 */
public interface PaymentRefundOrderService {

    /**
     * 分页查询支付退款订单
     *
     * @param query 查询条件
     * @return 支付退款订单分页结果
     */
    IPage<PaymentRefundOrder> pageQuery(PaymentRefundOrderQuery query);

    /**
     * 根据退款单号查询支付退款订单
     *
     * @param refundNo 退款单号
     * @return 支付退款订单
     */
    PaymentRefundOrder getPaymentRefundOrderByRefundNo(String refundNo);

    /**
     * 根据业务订单查询退款单，不存在时返回null
     *
     * @param bizType    业务类型
     * @param bizOrderId 业务订单ID
     * @return 支付退款订单
     */
    PaymentRefundOrder findByBizOrder(PaymentBizTypeEnum bizType, Long bizOrderId);

    /**
     * 保存已经构建完成的支付退款订单
     *
     * @param refundOrder 支付退款订单
     */
    void addPaymentRefundOrder(PaymentRefundOrder refundOrder);

    /**
     * 尝试将退款订单更新为退款成功。
     *
     * @param refundNo      退款单号
     * @param userId        退款用户ID
     * @param thirdRefundNo 第三方退款流水号
     * @param refundTime    退款成功时间
     * @return 是否更新成功
     */
    boolean tryUpdateRefundSuccess(String refundNo, Long userId, String thirdRefundNo, LocalDateTime refundTime);
}
