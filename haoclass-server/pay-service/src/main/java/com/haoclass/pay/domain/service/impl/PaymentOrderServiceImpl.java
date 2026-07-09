package com.haoclass.pay.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.domain.model.query.PaymentOrderQuery;
import com.haoclass.pay.domain.service.PaymentOrderService;
import com.haoclass.pay.infrastructure.common.enums.PaymentBizTypeEnum;
import com.haoclass.pay.infrastructure.common.enums.PaymentStatusEnum;
import com.haoclass.pay.infrastructure.persistence.mapper.PaymentOrderMapper;
import com.haoclass.pay.infrastructure.persistence.po.PaymentOrder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 支付订单领域服务实现。
 */
@Service
@Slf4j
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder>
        implements PaymentOrderService {

    @Override
    public IPage<PaymentOrder> pageQuery(PaymentOrderQuery query) {
        PaymentOrderQuery safeQuery = query == null ? new PaymentOrderQuery() : query;
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

        LambdaQueryWrapper<PaymentOrder> wrapper = Wrappers.lambdaQuery(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(StringUtils.hasText(safeQuery.getPaymentNo()), PaymentOrder::getPaymentNo, safeQuery.getPaymentNo())
                .eq(Objects.nonNull(safeQuery.getBizType()), PaymentOrder::getBizType, safeQuery.getBizType())
                .eq(StringUtils.hasText(safeQuery.getBizOrderNo()), PaymentOrder::getBizOrderNo, safeQuery.getBizOrderNo())
                .eq(Objects.nonNull(safeQuery.getUserId()), PaymentOrder::getUserId, safeQuery.getUserId())
                .eq(Objects.nonNull(safeQuery.getPayChannel()), PaymentOrder::getPayChannel, safeQuery.getPayChannel())
                .eq(Objects.nonNull(safeQuery.getStatus()), PaymentOrder::getStatus, safeQuery.getStatus())
                .eq(StringUtils.hasText(safeQuery.getThirdTradeNo()), PaymentOrder::getThirdTradeNo, safeQuery.getThirdTradeNo())
                .ge(Objects.nonNull(safeQuery.getCreateTimeStart()), PaymentOrder::getCreateTime, safeQuery.getCreateTimeStart())
                .le(Objects.nonNull(safeQuery.getCreateTimeEnd()), PaymentOrder::getCreateTime, safeQuery.getCreateTimeEnd())
                .orderByDesc(PaymentOrder::getCreateTime);

        return this.page(new Page<>(safeQuery.getCurrent(), safeQuery.getSize()), wrapper);
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentNo(String paymentNo) {
        if (!StringUtils.hasText(paymentNo)) {
            throw BusinessException.badRequest("支付单号不能为空");
        }

        PaymentOrder paymentOrder = this.getOne(Wrappers.lambdaQuery(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo));
        if (paymentOrder == null) {
            throw BusinessException.notFound("支付单不存在");
        }
        return paymentOrder;
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentNo(String paymentNo, Long userId) {
        if (!StringUtils.hasText(paymentNo) || userId == null) {
            throw BusinessException.badRequest("支付单号和用户ID不能为空");
        }
        PaymentOrder paymentOrder = this.getOne(Wrappers.lambdaQuery(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo)
                .eq(PaymentOrder::getUserId, userId));
        if (paymentOrder == null) {
            throw BusinessException.notFound("支付单不存在");
        }
        return paymentOrder;
    }

    @Override
    public PaymentOrder findByBizOrder(PaymentBizTypeEnum bizType, Long bizOrderId) {
        if (bizType == null || bizOrderId == null) {
            return null;
        }

        return this.getOne(Wrappers.lambdaQuery(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getBizType, bizType)
                .eq(PaymentOrder::getBizOrderId, bizOrderId));
    }

    @Override
    public void addPaymentOrder(PaymentOrder paymentOrder) {
        if (paymentOrder == null) {
            throw BusinessException.badRequest("支付订单不能为空");
        }
        if (!this.save(paymentOrder)) {
            throw new BusinessException("创建支付订单失败");
        }
    }

    @Override
    public void updateCodeUrl(String paymentNo, Long userId, String codeUrl) {
        if (!StringUtils.hasText(paymentNo) || userId == null || !StringUtils.hasText(codeUrl)) {
            throw BusinessException.badRequest("支付二维码参数不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean updated = this.update(Wrappers.lambdaUpdate(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo)
                .eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .set(PaymentOrder::getCodeUrl, codeUrl)
                .set(PaymentOrder::getUpdatedUser, userId)
                .set(PaymentOrder::getUpdateTime, now));

        if (!updated) {
            throw BusinessException.badRequest("更新支付二维码失败");
        }
    }

    @Override
    public void updatePaySuccess(String paymentNo, Long userId, String thirdTradeNo, LocalDateTime payTime) {
        LambdaUpdateWrapper<PaymentOrder> wrapper = Wrappers.lambdaUpdate(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo)
                .eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .gt(PaymentOrder::getExpireTime, payTime)
                .set(PaymentOrder::getStatus, PaymentStatusEnum.SUCCESS)
                .set(PaymentOrder::getUpdatedUser, userId)
                .set(PaymentOrder::getUpdateTime, payTime)
                .set(PaymentOrder::getPayTime, payTime)
                .set(PaymentOrder::getThirdTradeNo, thirdTradeNo);
        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.badRequest("更新订单支付成功失败");
        }
    }

    @Override
    public void updatePayFailed(String paymentNo, Long userId, LocalDateTime payTime) {
        LambdaUpdateWrapper<PaymentOrder> wrapper = Wrappers.lambdaUpdate(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo)
                .eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .gt(PaymentOrder::getExpireTime, payTime)
                .set(PaymentOrder::getStatus, PaymentStatusEnum.FAILED)
                .set(PaymentOrder::getUpdatedUser, userId)
                .set(PaymentOrder::getUpdateTime, payTime)
                .set(PaymentOrder::getCloseTime, payTime);
        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.badRequest("更新订单状态为支付失败时失败");
        }
    }

    @Override
    public boolean tryUpdatePaySuccess(String paymentNo, Long userId, String thirdTradeNo, LocalDateTime payTime) {
        LambdaUpdateWrapper<PaymentOrder> wrapper = Wrappers.lambdaUpdate(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getPaymentNo, paymentNo)
                .eq(PaymentOrder::getUserId, userId)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .gt(PaymentOrder::getExpireTime, payTime)
                .set(PaymentOrder::getStatus, PaymentStatusEnum.SUCCESS)
                .set(PaymentOrder::getUpdatedUser, userId)
                .set(PaymentOrder::getUpdateTime, payTime)
                .set(PaymentOrder::getPayTime, payTime)
                .set(PaymentOrder::getThirdTradeNo, thirdTradeNo);
        return this.update(wrapper);
    }

    @Override
    public boolean updateExpiredOrder(Long id, LocalDateTime closeTime) {
        if (id == null) {
            return false;
        }
        LambdaUpdateWrapper<PaymentOrder> wrapper = Wrappers.lambdaUpdate(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getId, id)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .le(PaymentOrder::getExpireTime, closeTime)
                .set(PaymentOrder::getStatus, PaymentStatusEnum.CLOSED)
                .set(PaymentOrder::getCloseTime, closeTime)
                .set(PaymentOrder::getUpdatedUser, 0L)
                .set(PaymentOrder::getUpdateTime, closeTime);

        return this.update(wrapper);
    }

    @Override
    public List<PaymentOrder> findExpiredOrders(int batchSize) {
        LocalDateTime now = LocalDateTime.now();
        int safeBatchSize = batchSize <= 0 ? 500 : Math.min(batchSize, 5000);
        LambdaQueryWrapper<PaymentOrder> wrapper = Wrappers.lambdaQuery(PaymentOrder.class)
                .eq(PaymentOrder::getDeleted, 0)
                .eq(PaymentOrder::getStatus, PaymentStatusEnum.PENDING)
                .le(PaymentOrder::getExpireTime, now)
                .orderByAsc(PaymentOrder::getExpireTime)
                .last("limit " + safeBatchSize);

        return this.list(wrapper);
    }
}
