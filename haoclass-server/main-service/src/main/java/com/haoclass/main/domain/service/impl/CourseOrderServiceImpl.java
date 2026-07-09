package com.haoclass.main.domain.service.impl;

import com.alibaba.cola.statemachine.StateMachine;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.domain.service.CourseOrderService;
import com.haoclass.main.domain.service.statemachine.component.CourseOrderStateMachine;
import com.haoclass.main.domain.service.statemachine.context.CourseOrderContext;
import com.haoclass.main.domain.service.statemachine.support.CourseOrderEventEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.CourseOrderMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseOrderServiceImpl extends ServiceImpl<CourseOrderMapper, CourseOrder> implements CourseOrderService {

    @Resource(name = CourseOrderStateMachine.MACHINE_ID)
    private StateMachine<CourseOrderStatusEnum, CourseOrderEventEnum, CourseOrderContext> courseOrderStateMachine;


    @Override
    public IPage<CourseOrder> pageQuery(CourseOrderQuery query) {
        if (Objects.isNull(query)) {
            query = new CourseOrderQuery();
        }
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1L);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10L);
        }
        if (query.getSize() > 100) {
            query.setSize(100L);
        }
        if (query.getCreateTimeStart() != null
                && query.getCreateTimeEnd() != null
                && query.getCreateTimeStart().isAfter(query.getCreateTimeEnd())) {
            throw BusinessException.badRequest("开始时间不能晚于结束时间");
        }

        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getDeleted, 0)
                .eq(StringUtils.hasText(query.getOrderNo()), CourseOrder::getOrderNo, query.getOrderNo())
                .eq(Objects.nonNull(query.getUserId()), CourseOrder::getUserId, query.getUserId())
                .eq(Objects.nonNull(query.getCourseId()), CourseOrder::getCourseId, query.getCourseId())
                .eq(Objects.nonNull(query.getStatus()), CourseOrder::getStatus, query.getStatus())
                .eq(Objects.nonNull(query.getPayType()), CourseOrder::getPayType, query.getPayType())
                .ge(Objects.nonNull(query.getCreateTimeStart()), CourseOrder::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), CourseOrder::getCreateTime, query.getCreateTimeEnd())
                .orderByDesc(CourseOrder::getCreateTime);

        return this.page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public CourseOrder getCourseOrderById(Long id) {
        CourseOrder courseOrder = this.getOne(Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getDeleted, 0));
        if (Objects.isNull(courseOrder)) {
            throw BusinessException.notFound("订单不存在");
        }

        return courseOrder;
    }

    @Override
    public CourseOrder getCourseOrderById(Long id, Long userId) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getDeleted, 0);
        CourseOrder courseOrder = this.getOne(wrapper);
        if(Objects.isNull(courseOrder)) {
            throw BusinessException.notFound("不存在该订单");
        }

        return courseOrder;
    }

    @Override
    public CourseOrder findPendingOrder(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getCourseId, courseId)
                .eq(CourseOrder::getStatus, CourseOrderStatusEnum.PENDING_PAYMENT)
                .eq(CourseOrder::getDeleted, 0)
                .gt(CourseOrder::getExpireTime, LocalDateTime.now())
                .orderByDesc(CourseOrder::getCreateTime);

        return this.getOne(wrapper, false);
    }



    @Override
    public void addCourseOrder(CourseOrder courseOrder) {
        boolean saved = this.save(courseOrder);
        if (!saved) {
            throw BusinessException.badRequest("创建订单失败");
        }
    }

    @Override
    public CourseOrder getPendingOrderById(Long id, Long userId) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getStatus, CourseOrderStatusEnum.PENDING_PAYMENT)
                .eq(CourseOrder::getDeleted, 0)
                .gt(CourseOrder::getExpireTime, LocalDateTime.now());

        CourseOrder courseOrder = this.getOne(wrapper);
        if(Objects.isNull(courseOrder)) {
            throw BusinessException.badRequest("待支付订单不存在或已过期");
        }

        return courseOrder;
    }

    @Override
    public void payPendingOrder(Long id, Long userId, String thirdTradeNo, CourseOrderPayTypeEnum payType,
                                LocalDateTime payTime, CourseOrder pendingOrder) {
        CourseOrderStatusEnum currentStatus = pendingOrder.getStatus();

        CourseOrderContext context = new CourseOrderContext();
        context.setId(id);
        context.setUserId(userId);
        context.setCurrentStatus(currentStatus);

        CourseOrderStatusEnum targetStatus =
                courseOrderStateMachine.fireEvent(
                        currentStatus,
                        CourseOrderEventEnum.PAYMENT_SUCCESS,
                        context
                );

        if (targetStatus == null) {
            throw BusinessException.badRequest("状态流转失败");
        }

        LambdaUpdateWrapper<CourseOrder> wrapper = Wrappers.lambdaUpdate(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getDeleted, 0)
                .eq(CourseOrder::getStatus, currentStatus)
                .gt(CourseOrder::getExpireTime, payTime)
                .set(CourseOrder::getPayType, payType)
                .set(CourseOrder::getThirdTradeNo, thirdTradeNo)
                .set(CourseOrder::getPayTime, payTime)
                .set(CourseOrder::getStatus, targetStatus)
                .set(CourseOrder::getUpdateTime, payTime)
                .set(CourseOrder::getUpdatedUser, userId);

        boolean updated = this.update(wrapper);

        if(!updated) {
            throw BusinessException.badRequest("订单状态已变化，请勿重复支付");
        }
    }

    @Override
    public void refundPaidOrder(Long id, Long userId, LocalDateTime refundTime, CourseOrder paidOrder) {
        CourseOrderStatusEnum currentStatus = paidOrder.getStatus();

        CourseOrderContext context = new CourseOrderContext();
        context.setId(id);
        context.setUserId(userId);
        context.setCurrentStatus(currentStatus);

        CourseOrderStatusEnum targetStatus = courseOrderStateMachine.fireEvent(
                currentStatus,
                CourseOrderEventEnum.REFUND_SUCCESS,
                context
        );

        if (targetStatus == null) {
            throw BusinessException.badRequest("该订单状态不能流转为已退款");
        }

        boolean updated = this.update(Wrappers.lambdaUpdate(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getDeleted, 0)
                .eq(CourseOrder::getStatus, currentStatus)
                .set(CourseOrder::getStatus, targetStatus)
                .set(CourseOrder::getRefundTime, refundTime)
                .set(CourseOrder::getUpdateTime, refundTime)
                .set(CourseOrder::getUpdatedUser, userId));

        if (!updated) {
            throw BusinessException.badRequest("订单状态已变化，退款失败");
        }
    }

    @Override
    public void closeExpiredPendingOrders(Set<Long> ids, LocalDateTime now) {
        CourseOrderStatusEnum currentStatus = CourseOrderStatusEnum.PENDING_PAYMENT;

        CourseOrderContext context = new CourseOrderContext();
        context.setCurrentStatus(currentStatus);

        CourseOrderStatusEnum targetStatus = courseOrderStateMachine.fireEvent(
                currentStatus,
                CourseOrderEventEnum.TIMEOUT_CLOSE,
                context
        );

        if (targetStatus == null) {
            throw BusinessException.badRequest("该状态不能流转");
        }

        this.update(Wrappers.lambdaUpdate(CourseOrder.class)
                .eq(CourseOrder::getDeleted, 0)
                .in(CourseOrder::getId, ids)
                .eq(CourseOrder::getStatus, currentStatus)
                .le(CourseOrder::getExpireTime, now)
                .set(CourseOrder::getStatus, targetStatus)
                .set(CourseOrder::getUpdateTime, now)
                .set(CourseOrder::getUpdatedUser, 0L));
    }

    @Override
    public boolean closePendingOrderByPaymentClosed(Long id, LocalDateTime closeTime) {
        CourseOrderStatusEnum currentStatus = CourseOrderStatusEnum.PENDING_PAYMENT;

        CourseOrderContext context = new CourseOrderContext();
        context.setId(id);
        context.setCurrentStatus(currentStatus);

        CourseOrderStatusEnum targetStatus = courseOrderStateMachine.fireEvent(
                currentStatus,
                CourseOrderEventEnum.TIMEOUT_CLOSE,
                context
        );

        if (targetStatus == null) {
            throw BusinessException.badRequest("该订单状态不能流转");
        }

        return this.update(Wrappers.lambdaUpdate(CourseOrder.class)
                .eq(CourseOrder::getDeleted, 0)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getStatus, currentStatus)
                .set(CourseOrder::getStatus, targetStatus)
                .set(CourseOrder::getUpdateTime, closeTime)
                .set(CourseOrder::getUpdatedUser, 0L));
    }

    @Override
    public void closePendingOrders(LocalDateTime now, Long id) {
        Long userId = SecurityUserHolder.getUserId();
        CourseOrder pendingOrder = this.getPendingOrderById(id, userId);
        CourseOrderStatusEnum currentStatus = pendingOrder.getStatus();
        CourseOrderContext context = new CourseOrderContext();
        context.setCourseId(pendingOrder.getCourseId());
        context.setId(id);
        context.setCurrentStatus(currentStatus);
        context.setUserId(userId);
        CourseOrderStatusEnum targetStatus = courseOrderStateMachine.fireEvent(
                currentStatus,
                CourseOrderEventEnum.USER_CANCEL,
                context
        );
        if(targetStatus == null) {
            throw BusinessException.badRequest("该订单状态不能流转");
        }
        LambdaUpdateWrapper<CourseOrder> wrapper = Wrappers.lambdaUpdate(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getDeleted, 0)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getStatus, currentStatus)
                .gt(CourseOrder::getExpireTime, now)
                .set(CourseOrder::getCancelTime, now)
                .set(CourseOrder::getStatus, targetStatus)
                .set(CourseOrder::getUpdateTime, now)
                .set(CourseOrder::getUpdatedUser, userId);

        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.notFound("不存在该订单或订单更新失败");
        }
    }

    @Override
    public List<CourseOrder> findPendingOrders(LocalDateTime now) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getDeleted, 0)
                .eq(CourseOrder::getStatus, CourseOrderStatusEnum.PENDING_PAYMENT)
                .le(CourseOrder::getExpireTime, now);

        return this.list(wrapper);
    }
}
