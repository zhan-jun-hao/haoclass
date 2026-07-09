package com.haoclass.pay.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.pay.domain.model.query.PaymentOrderQuery;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 支付订单领域服务
 */
public interface PaymentOrderService {

    /**
     * 分页查询支付订单
     *
     * @param query 查询条件
     * @return 支付订单分页结果
     */
    IPage<PaymentOrder> pageQuery(PaymentOrderQuery query);

    /**
     * 根据支付单号查询支付订单
     *
     * @param paymentNo 支付单号
     * @return 支付订单
     */
    PaymentOrder getPaymentOrderByPaymentNo(String paymentNo);

    /**
     * 查询当前用户所属的支付订单
     *
     * @param paymentNo 支付单号
     * @param userId    用户ID
     * @return 支付订单
     */
    PaymentOrder getPaymentOrderByPaymentNo(String paymentNo, Long userId);

    /**
     * 根据业务订单查询支付订单，不存在时返回null
     *
     * @param bizType    业务类型
     * @param bizOrderId 业务订单ID
     * @return 支付订单
     */
    PaymentOrder findByBizOrder(PaymentBizTypeEnum bizType, Long bizOrderId);

    /**
     * 保存已经构建完成的支付订单
     *
     * @param paymentOrder 支付订单
     */
    void addPaymentOrder(PaymentOrder paymentOrder);

    /**
     * 更新支付二维码内容
     *
     * @param paymentNo 支付单号
     * @param userId    支付用户ID
     * @param codeUrl   微信Native支付二维码内容
     */
    void updateCodeUrl(String paymentNo, Long userId, String codeUrl);

    /**
     * 更新支付订单成功
     *
     * @param paymentNo
     * @param userId
     * @param thirdTradeNo
     * @param payTime
     */
    void updatePaySuccess(String paymentNo, Long userId, String thirdTradeNo, LocalDateTime payTime);

    /**
     * 更新支付订单失败
     *
     * @param paymentNo
     * @param userId
     * @param payTime
     */
    void updatePayFailed(String paymentNo, Long userId, LocalDateTime payTime);

    /**
     * 尝试更新支付订单服务
     *
     * @param paymentNo
     * @param userId
     * @param thirdTradeNo
     * @param payTime
     * @return
     */
    boolean tryUpdatePaySuccess(String paymentNo, Long userId, String thirdTradeNo, LocalDateTime payTime);

    /**
     * 更新过期支付订单状态为已超时
     */
    boolean updateExpiredOrder(Long id, LocalDateTime closeTime);

    /**
     * 查询过期的支付订单
     *
     * @return
     */
    List<PaymentOrder> findExpiredOrders(int batchSize);
}
