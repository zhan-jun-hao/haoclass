package com.haoclass.pay.application.service.inner.impl;

import cn.hutool.core.util.IdUtil;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.api.dto.request.CreateRefundRequest;
import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import com.haoclass.pay.application.converter.inner.InnerPaymentRefundOrderConverter;
import com.haoclass.pay.application.service.inner.InnerPaymentRefundApplicationService;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.domain.service.PaymentRefundOrderService;
import com.haoclass.pay.infrastructure.adapter.PayChannelAdapter;
import com.haoclass.pay.infrastructure.adapter.PayChannelAdapterFactory;
import com.haoclass.pay.infrastructure.adapter.dto.ChannelPaymentRefundResultDto;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 内部服务-退款应用服务实现。
 */
@Service
@RequiredArgsConstructor
public class InnerPaymentRefundApplicationServiceImpl implements InnerPaymentRefundApplicationService {

    private final PaymentOrderService paymentOrderService;
    private final PaymentRefundOrderService paymentRefundOrderService;
    private final RedissonClient redissonClient;
    private final PayChannelAdapterFactory payChannelAdapterFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateRefundResponse createRefund(CreateRefundRequest request) {
        PaymentBizTypeEnum bizType = validateRequest(request);
        String lockKey = "pay:create:refund:" + request.getBizType() + ":" + request.getBizOrderId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!locked) {
                throw BusinessException.badRequest("退款请求处理中，请稍后重试");
            }

            PaymentRefundOrder existing = paymentRefundOrderService.findByBizOrder(bizType, request.getBizOrderId());
            if (existing != null) {
                validateExistingRefundConsistency(existing, request);
                return converter(existing);
            }

            PaymentOrder paymentOrder = findPaymentOrder(request, bizType);
            validatePaymentOrderCanRefund(paymentOrder, request, bizType);

            PaymentRefundOrder refundOrder = buildRefundOrder(paymentOrder, request);
            paymentRefundOrderService.addPaymentRefundOrder(refundOrder);
            createChannelRefund(refundOrder);
            return converter(refundOrder);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("创建退款单被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private PaymentOrder findPaymentOrder(CreateRefundRequest request, PaymentBizTypeEnum bizType) {
        if (StringUtils.hasText(request.getPaymentNo())) {
            return paymentOrderService.getPaymentOrderByPaymentNo(request.getPaymentNo());
        }

        PaymentOrder paymentOrder = paymentOrderService.findByBizOrder(bizType, request.getBizOrderId());
        if (paymentOrder == null) {
            throw BusinessException.notFound("原支付单不存在");
        }
        return paymentOrder;
    }

    private PaymentRefundOrder buildRefundOrder(PaymentOrder paymentOrder, CreateRefundRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Long refundOrderId = IdUtil.getSnowflakeNextId();
        PaymentRefundOrder refundOrder = InnerPaymentRefundOrderConverter.INSTANCE.orderPoToRefundPo(paymentOrder);
        refundOrder.setId(refundOrderId);
        refundOrder.setRefundNo(generateRefundNo(refundOrderId));
        refundOrder.setRefundAmount(request.getRefundAmount());
        refundOrder.setStatus(PaymentRefundStatusEnum.PROCESSING);
        refundOrder.setThirdTradeNo(paymentOrder.getThirdTradeNo());
        refundOrder.setThirdRefundNo("");
        refundOrder.setRefundReason(request.getRefundReason());
        refundOrder.setFailureReason("");
        refundOrder.setApplyTime(now);
        refundOrder.setRefundTime(null);
        refundOrder.setCreateTime(now);
        refundOrder.setUpdateTime(now);
        refundOrder.setCreatedUser(paymentOrder.getUserId());
        refundOrder.setUpdatedUser(paymentOrder.getUserId());
        return refundOrder;
    }

    /**
     * 创建第三方退款
     */
    private void createChannelRefund(PaymentRefundOrder refundOrder) {
        PayChannelAdapter adapter = payChannelAdapterFactory.getAdapter(refundOrder.getPayChannel());
        ChannelPaymentRefundResultDto result = adapter.createRefund(refundOrder);
        if (result == null || !Boolean.TRUE.equals(result.getAccepted())) {
            throw BusinessException.badRequest("第三方退款申请失败");
        }
    }

    public String generateRefundNo(Long paymentOrderId) {
        // 时间戳 + 随机数
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        return "REFUND" + timestamp + paymentOrderId;
    }

    /**
     * 校验创建退款单请求
     *
     * @param request 创建退款单请求
     * @return 支付业务类型
     */
    private PaymentBizTypeEnum validateRequest(CreateRefundRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("创建退款单请求不能为空");
        }
        PaymentBizTypeEnum bizType = PaymentBizTypeEnum.of(request.getBizType());
        if (bizType == null) {
            throw BusinessException.badRequest("退款业务类型错误");
        }
        if (request.getBizOrderId() == null) {
            throw BusinessException.badRequest("业务订单ID不能为空");
        }
        if (!StringUtils.hasText(request.getBizOrderNo())) {
            throw BusinessException.badRequest("业务订单号不能为空");
        }
        if (request.getUserId() == null) {
            throw BusinessException.badRequest("退款用户ID不能为空");
        }
        if (request.getRefundAmount() == null || request.getRefundAmount() <= 0) {
            throw BusinessException.badRequest("退款金额必须大于0");
        }
        return bizType;
    }

    /**
     * 校验原支付单是否允许退款
     *
     * @param paymentOrder 原支付单
     * @param request      创建退款单请求
     * @param bizType      业务类型
     */
    private void validatePaymentOrderCanRefund(PaymentOrder paymentOrder, CreateRefundRequest request,
                                               PaymentBizTypeEnum bizType) {
        if (paymentOrder.getStatus() != PaymentStatusEnum.SUCCESS) {
            throw BusinessException.badRequest("只有支付成功的支付单才允许退款");
        }
        if (!Objects.equals(paymentOrder.getBizType(), bizType)) {
            throw BusinessException.badRequest("退款业务类型与原支付单不一致");
        }
        if (!Objects.equals(paymentOrder.getBizOrderId(), request.getBizOrderId())) {
            throw BusinessException.badRequest("退款业务订单ID与原支付单不一致");
        }
        if (!Objects.equals(paymentOrder.getBizOrderNo(), request.getBizOrderNo())) {
            throw BusinessException.badRequest("退款业务订单号与原支付单不一致");
        }
        if (!Objects.equals(paymentOrder.getUserId(), request.getUserId())) {
            throw BusinessException.badRequest("退款用户与原支付用户不一致");
        }
        if (request.getRefundAmount() > paymentOrder.getPayAmount()) {
            throw BusinessException.badRequest("退款金额不能大于原支付金额");
        }
    }

    /**
     * 校验重复退款请求与已有退款单是否一致。
     *
     * @param existing 已有退款单
     * @param request  创建退款单请求
     */
    private void validateExistingRefundConsistency(PaymentRefundOrder existing, CreateRefundRequest request) {
        if (!Objects.equals(existing.getUserId(), request.getUserId())) {
            throw BusinessException.badRequest("退款用户与已有退款单不一致");
        }
        if (!Objects.equals(existing.getBizOrderNo(), request.getBizOrderNo())) {
            throw BusinessException.badRequest("业务订单号与已有退款单不一致");
        }
        if (!Objects.equals(existing.getRefundAmount(), request.getRefundAmount())) {
            throw BusinessException.badRequest("退款金额与已有退款单不一致");
        }
        if (StringUtils.hasText(request.getPaymentNo())
                && !Objects.equals(existing.getPaymentNo(), request.getPaymentNo())) {
            throw BusinessException.badRequest("原支付单号与已有退款单不一致");
        }
    }

    private CreateRefundResponse converter(PaymentRefundOrder refundOrder) {
        CreateRefundResponse response = InnerPaymentRefundOrderConverter.INSTANCE.poToInnerResp(refundOrder);
        response.setStatus(refundOrder.getStatus().getCode());
        return response;
    }
}
