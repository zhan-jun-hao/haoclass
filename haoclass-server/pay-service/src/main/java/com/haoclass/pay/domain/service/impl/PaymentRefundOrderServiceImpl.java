package com.haoclass.pay.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.domain.model.query.PaymentRefundOrderQuery;
import com.haoclass.pay.domain.service.PaymentRefundOrderService;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentRefundStatusEnum;
import com.haoclass.pay.infrastructure.persistence.mapper.PaymentRefundOrderMapper;
import com.haoclass.pay.infrastructure.persistence.po.PaymentRefundOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 支付退款订单领域服务实现。
 */
@Service
public class PaymentRefundOrderServiceImpl extends ServiceImpl<PaymentRefundOrderMapper, PaymentRefundOrder>
        implements PaymentRefundOrderService {

    @Override
    public IPage<PaymentRefundOrder> pageQuery(PaymentRefundOrderQuery query) {
        PaymentRefundOrderQuery safeQuery = query == null ? new PaymentRefundOrderQuery() : query;
        if (safeQuery.getCurrent() == null || safeQuery.getCurrent() < 1) {
            safeQuery.setCurrent(1L);
        }
        if (safeQuery.getSize() == null || safeQuery.getSize() < 1) {
            safeQuery.setSize(10L);
        }
        if (safeQuery.getSize() > 100) {
            safeQuery.setSize(100L);
        }
        if (safeQuery.getCreateTimeStart() != null
                && safeQuery.getCreateTimeEnd() != null
                && safeQuery.getCreateTimeStart().isAfter(safeQuery.getCreateTimeEnd())) {
            throw BusinessException.badRequest("开始时间不能晚于结束时间");
        }

        LambdaQueryWrapper<PaymentRefundOrder> wrapper = Wrappers.lambdaQuery(PaymentRefundOrder.class)
                .eq(PaymentRefundOrder::getDeleted, 0)
                .eq(StringUtils.hasText(safeQuery.getRefundNo()), PaymentRefundOrder::getRefundNo, safeQuery.getRefundNo())
                .eq(StringUtils.hasText(safeQuery.getPaymentNo()), PaymentRefundOrder::getPaymentNo, safeQuery.getPaymentNo())
                .eq(Objects.nonNull(safeQuery.getBizType()), PaymentRefundOrder::getBizType, PaymentBizTypeEnum.of(safeQuery.getBizType()))
                .eq(StringUtils.hasText(safeQuery.getBizOrderNo()), PaymentRefundOrder::getBizOrderNo, safeQuery.getBizOrderNo())
                .eq(Objects.nonNull(safeQuery.getUserId()), PaymentRefundOrder::getUserId, safeQuery.getUserId())
                .eq(Objects.nonNull(safeQuery.getPayChannel()), PaymentRefundOrder::getPayChannel, PaymentChannelEnum.of(safeQuery.getPayChannel()))
                .eq(Objects.nonNull(safeQuery.getStatus()), PaymentRefundOrder::getStatus, PaymentRefundStatusEnum.of(safeQuery.getStatus()))
                .eq(StringUtils.hasText(safeQuery.getThirdRefundNo()), PaymentRefundOrder::getThirdRefundNo, safeQuery.getThirdRefundNo())
                .ge(Objects.nonNull(safeQuery.getCreateTimeStart()), PaymentRefundOrder::getCreateTime, safeQuery.getCreateTimeStart())
                .le(Objects.nonNull(safeQuery.getCreateTimeEnd()), PaymentRefundOrder::getCreateTime, safeQuery.getCreateTimeEnd())
                .orderByDesc(PaymentRefundOrder::getCreateTime);

        return this.page(new Page<>(safeQuery.getCurrent(), safeQuery.getSize()), wrapper);
    }

    @Override
    public PaymentRefundOrder getPaymentRefundOrderByRefundNo(String refundNo) {
        if (!StringUtils.hasText(refundNo)) {
            throw BusinessException.badRequest("退款单号不能为空");
        }

        PaymentRefundOrder refundOrder = this.getOne(Wrappers.lambdaQuery(PaymentRefundOrder.class)
                .eq(PaymentRefundOrder::getDeleted, 0)
                .eq(PaymentRefundOrder::getRefundNo, refundNo));

        if (Objects.isNull(refundOrder)) {
            throw BusinessException.notFound("退款单不存在");
        }
        return refundOrder;
    }

    @Override
    public PaymentRefundOrder findByBizOrder(PaymentBizTypeEnum bizType, Long bizOrderId) {
        if (bizType == null || bizOrderId == null) {
            return null;
        }

        return this.getOne(Wrappers.lambdaQuery(PaymentRefundOrder.class)
                .eq(PaymentRefundOrder::getDeleted, 0)
                .eq(PaymentRefundOrder::getBizType, bizType)
                .eq(PaymentRefundOrder::getBizOrderId, bizOrderId));
    }

    @Override
    public void addPaymentRefundOrder(PaymentRefundOrder refundOrder) {
        if (refundOrder == null) {
            throw BusinessException.badRequest("退款订单不能为空");
        }
        if (!this.save(refundOrder)) {
            throw new BusinessException("创建退款订单失败");
        }
    }

    @Override
    public boolean tryUpdateRefundSuccess(String refundNo, Long userId, String thirdRefundNo, LocalDateTime refundTime) {
        LambdaUpdateWrapper<PaymentRefundOrder> wrapper = Wrappers.lambdaUpdate(PaymentRefundOrder.class)
                .eq(PaymentRefundOrder::getDeleted, 0)
                .eq(PaymentRefundOrder::getRefundNo, refundNo)
                .eq(PaymentRefundOrder::getUserId, userId)
                .in(PaymentRefundOrder::getStatus,
                        PaymentRefundStatusEnum.PENDING,
                        PaymentRefundStatusEnum.PROCESSING)
                .set(PaymentRefundOrder::getStatus, PaymentRefundStatusEnum.SUCCESS)
                .set(PaymentRefundOrder::getUpdatedUser, userId)
                .set(PaymentRefundOrder::getUpdateTime, refundTime)
                .set(PaymentRefundOrder::getRefundTime, refundTime)
                .set(PaymentRefundOrder::getThirdRefundNo, thirdRefundNo);
        return this.update(wrapper);
    }
}
