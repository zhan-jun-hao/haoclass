package com.haoclass.main.application.service.inner.impl;

import cn.hutool.core.util.IdUtil;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.result.Result;
import com.haoclass.coupon.api.client.CouponFeignClient;
import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.main.application.service.inner.InnerCourseOrderApplicationService;
import com.haoclass.main.domain.service.CourseOrderService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.domain.service.CourseUserService;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserSourceTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import com.haoclass.pay.api.dto.request.PaymentClosedRequest;
import com.haoclass.pay.api.dto.request.PaymentRefundSuccessRequest;
import com.haoclass.pay.api.dto.request.PaymentSuccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InnerCourseOrderApplicationServiceImpl implements InnerCourseOrderApplicationService {

    private final CourseOrderService courseOrderService;
    private final CourseUserService courseUserService;
    private final CourseService courseService;
    private final CouponFeignClient couponFeignClient;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeExpiredPendingOrders(Set<Long> ids, LocalDateTime now) {
        courseOrderService.closeExpiredPendingOrders(ids, now);
    }

    @Override
    public void handlePaymentSuccess(PaymentSuccessRequest request) {
        validatePaymentSuccessRequest(request);
        // 1.先处理本地事务
        CourseOrder courseOrder = transactionTemplate.execute(status ->
                handlePaymentSuccessInLocalTransaction(request));

        // 2.再处理优惠券事务
        if (courseOrder != null && courseOrder.getUserCouponId() != null) {
            useCoupon(courseOrder.getUserId(), courseOrder.getId(), courseOrder.getUserCouponId());
        }
    }

    @Override
    public void handlePaymentClosed(PaymentClosedRequest request) {
        validatePaymentClosedRequest(request);
        CourseOrder courseOrder = transactionTemplate.execute(status ->
                handlePaymentClosedInLocalTransaction(request));

        if (courseOrder != null && courseOrder.getUserCouponId() != null) {
            returnCoupons(Set.of(courseOrder.getUserCouponId()));
        }
    }

    @Override
    public void handleRefundSuccess(PaymentRefundSuccessRequest request) {
        validateRefundSuccessRequest(request);
        // 1.退款
        CourseOrder courseOrder = transactionTemplate.execute(status ->
                handleRefundSuccessInLocalTransaction(request));

        // 2.返还优惠券
        if (courseOrder != null && courseOrder.getUserCouponId() != null) {
            returnCoupons(Set.of(courseOrder.getUserCouponId()));
        }
    }

    @Override
    public List<CourseOrder> findExpiredPendingOrders(LocalDateTime now) {
        return courseOrderService.findPendingOrders(now);
    }

    private CourseOrder handlePaymentSuccessInLocalTransaction(PaymentSuccessRequest request) {
        CourseOrder courseOrder = courseOrderService.getCourseOrderById(request.getBizOrderId());

        // 支付成功消息可能重复投递，已经履约的同一笔支付直接返回
        if (courseOrder.getStatus() == CourseOrderStatusEnum.PAID) {
            if (Objects.equals(courseOrder.getThirdTradeNo(), request.getThirdTradeNo())) {
                return courseOrder;
            }
            throw BusinessException.badRequest("课程订单已经被其他支付流水履约");
        }
        if (courseOrder.getStatus() != CourseOrderStatusEnum.PENDING_PAYMENT) {
            throw BusinessException.badRequest("当前课程订单状态不允许支付履约");
        }
        if (!Objects.equals(courseOrder.getPayAmount(), request.getPayAmount())) {
            throw BusinessException.badRequest("支付金额与课程订单金额不一致");
        }

        CourseOrderPayTypeEnum payType = CourseOrderPayTypeEnum.of(request.getPayChannel());
        if (payType == null || payType == CourseOrderPayTypeEnum.UNPAID) {
            throw BusinessException.badRequest("支付渠道不正确");
        }

        // 先以待支付状态作为条件更新课程订单，避免重复履约
        courseOrderService.payPendingOrder(courseOrder.getId(), courseOrder.getUserId(), request.getThirdTradeNo(),
                payType, request.getPayTime(), courseOrder);

        grantCourseAccess(courseOrder, request.getPayTime());
        courseService.updateBuyCountById(courseOrder.getCourseId());
        return courseOrder;
    }

    private CourseOrder handlePaymentClosedInLocalTransaction(PaymentClosedRequest request) {
        CourseOrder courseOrder = courseOrderService.getCourseOrderById(request.getBizOrderId());

        if (courseOrder.getStatus() == CourseOrderStatusEnum.PAID) {
            return null;
        }
        if (courseOrder.getStatus() == CourseOrderStatusEnum.CLOSED
                || courseOrder.getStatus() == CourseOrderStatusEnum.CANCELLED) {
            return courseOrder;
        }
        if (courseOrder.getStatus() != CourseOrderStatusEnum.PENDING_PAYMENT) {
            throw BusinessException.badRequest("当前课程订单状态不允许支付关闭");
        }
        if (!Objects.equals(courseOrder.getPayAmount(), request.getPayAmount())) {
            throw BusinessException.badRequest("支付金额与课程订单金额不一致");
        }

        boolean updated = courseOrderService.closePendingOrderByPaymentClosed(courseOrder.getId(), request.getCloseTime());
        if (!updated) {
            throw BusinessException.badRequest("课程订单状态已变化，关闭失败");
        }
        return courseOrder;
    }

    private CourseOrder handleRefundSuccessInLocalTransaction(PaymentRefundSuccessRequest request) {
        CourseOrder courseOrder = courseOrderService.getCourseOrderById(request.getBizOrderId());

        if (courseOrder.getStatus() == CourseOrderStatusEnum.REFUNDED) {
            return courseOrder;
        }
        if (courseOrder.getStatus() != CourseOrderStatusEnum.PAID) {
            throw BusinessException.badRequest("当前课程订单状态不允许退款履约");
        }
        if (!Objects.equals(courseOrder.getPayAmount(), request.getRefundAmount())) {
            throw BusinessException.badRequest("退款金额与课程订单实付金额不一致");
        }

        courseOrderService.refundPaidOrder(courseOrder.getId(), courseOrder.getUserId(),
                request.getRefundTime(), courseOrder);
        courseUserService.refundCourseUser(courseOrder.getUserId(), courseOrder.getCourseId(),
                courseOrder.getId(), request.getRefundTime());
        courseService.decreaseBuyCountById(courseOrder.getCourseId());
        return courseOrder;
    }

    private void validatePaymentSuccessRequest(PaymentSuccessRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("支付成功通知不能为空");
        }
        if (!Objects.equals(request.getBizType(), 1)) {
            throw BusinessException.badRequest("暂不支持该支付业务类型");
        }
        if (request.getBizOrderId() == null || !StringUtils.hasText(request.getPaymentNo())
                || !StringUtils.hasText(request.getThirdTradeNo())
                || request.getPayAmount() == null
                || request.getPayAmount() <= 0
                || request.getPayChannel() == null
                || request.getPayTime() == null) {
            throw BusinessException.badRequest("支付成功通知参数不完整");
        }
    }

    private void validatePaymentClosedRequest(PaymentClosedRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("支付关闭通知不能为空");
        }
        if (!Objects.equals(request.getBizType(), 1)) {
            throw BusinessException.badRequest("暂不支持该支付业务类型");
        }
        if (request.getBizOrderId() == null
                || !StringUtils.hasText(request.getPaymentNo())
                || request.getPayAmount() == null
                || request.getPayAmount() <= 0
                || request.getCloseTime() == null) {
            throw BusinessException.badRequest("支付关闭通知参数不完整");
        }
    }

    private void validateRefundSuccessRequest(PaymentRefundSuccessRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("退款成功通知不能为空");
        }
        if (!Objects.equals(request.getBizType(), 1)) {
            throw BusinessException.badRequest("暂不支持该退款业务类型");
        }
        if (request.getBizOrderId() == null
                || !StringUtils.hasText(request.getPaymentNo())
                || !StringUtils.hasText(request.getRefundNo())
                || !StringUtils.hasText(request.getThirdRefundNo())
                || request.getRefundAmount() == null
                || request.getRefundAmount() <= 0
                || request.getPayChannel() == null
                || request.getRefundTime() == null) {
            throw BusinessException.badRequest("退款成功通知参数不完整");
        }
    }

    private void grantCourseAccess(CourseOrder courseOrder, LocalDateTime payTime) {
        CourseUser courseUser =
                courseUserService.findByUserIdAndCourseId(courseOrder.getUserId(), courseOrder.getCourseId());
        if (courseUser == null) {
            courseUserService.addCourseUser(createCourseUser(courseOrder, payTime));
            return;
        }

        courseUser.setStatus(CourseUserStatusEnum.VALID);
        courseUser.setSourceType(CourseUserSourceTypeEnum.PURCHASE);
        courseUser.setOrderId(courseOrder.getId());
        courseUser.setExpireTime(null);
        courseUser.setGrantTime(payTime);
        courseUser.setUpdateTime(payTime);
        courseUser.setUpdatedUser(courseOrder.getUserId());
        courseUserService.updateCourseUser(courseUser);
    }

    private CourseUser createCourseUser(CourseOrder courseOrder, LocalDateTime payTime) {
        CourseUser courseUser = new CourseUser();
        courseUser.setId(IdUtil.getSnowflakeNextId());
        courseUser.setUserId(courseOrder.getUserId());
        courseUser.setCourseId(courseOrder.getCourseId());
        courseUser.setOrderId(courseOrder.getId());
        courseUser.setSourceType(CourseUserSourceTypeEnum.PURCHASE);
        courseUser.setStatus(CourseUserStatusEnum.VALID);
        courseUser.setGrantTime(payTime);
        courseUser.setExpireTime(null);
        courseUser.setCreatedUser(courseOrder.getUserId());
        courseUser.setUpdatedUser(courseOrder.getUserId());
        courseUser.setCreateTime(payTime);
        courseUser.setUpdateTime(payTime);
        courseUser.setDeleted(0);
        return courseUser;
    }

    private void useCoupon(Long userId, Long orderId, Long userCouponId) {
        ChosenCouponReqVo reqVo = new ChosenCouponReqVo();
        reqVo.setOrderId(orderId);
        reqVo.setUserCouponId(userCouponId);
        reqVo.setUserId(userId);
        Result<Void> result = couponFeignClient.useCoupon(reqVo);
        if (result == null || result.getCode() != 200) {
            String message = result == null ? "远程服务无响应" : result.getMsg();
            throw new BusinessException("核销优惠券失败: " + message);
        }
    }

    private void returnCoupons(Set<Long> userCouponIds) {
        Result<Void> result = couponFeignClient.returnCoupons(userCouponIds);
        if (result == null || result.getCode() != 200) {
            String message = result == null ? "远程服务无响应" : result.getMsg();
            throw new BusinessException("归还优惠券失败: " + message);
        }
    }
}
