package com.haoclass.main.application.service.client.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.context.UserContext;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.coupon.api.client.CouponFeignClient;
import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.main.application.converter.client.ClientCourseOrderConverter;
import com.haoclass.main.application.converter.inner.OrderAvailableCouponConverter;
import com.haoclass.main.application.service.client.ClientCourseOrderApplicationService;
import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.domain.service.CourseOrderService;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.domain.service.CourseUserService;
import com.haoclass.main.domain.service.UserService;
import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserSourceTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserStatusEnum;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderReqVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderAvailableCouponRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderDetailVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderPayRespVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderRefundRespVo;
import com.haoclass.pay.api.client.PayClientFeignClient;
import com.haoclass.pay.api.dto.request.CreatePaymentRequest;
import com.haoclass.pay.api.dto.request.CreateRefundRequest;
import com.haoclass.pay.api.dto.response.CreatePaymentResponse;
import com.haoclass.pay.api.dto.response.CreateRefundResponse;
import com.haoclass.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ClientCourseOrderApplicationServiceImpl implements ClientCourseOrderApplicationService {

    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CourseOrderService courseOrderService;
    private final CourseService courseService;
    private final CourseUserService courseUserService;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;
    private final UserService userService;
    private final CouponFeignClient couponFeignClient;
    private final PayClientFeignClient payClientFeignClient;

    /**
     * 创建课程订单
     *
     * @param reqVo 创建课程订单请求对象
     * @return 创建成功的订单信息
     */
    @Override
    public CourseOrderBasicVo createCourseOrder(ClientCourseOrderReqVo reqVo) {
        Long userId = SecurityUserHolder.getUserId();
        Long courseId = reqVo.getCourseId();
        String lockKey = "main-service:order:create:" + userId + ":" + courseId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 同一用户购买同一课程时串行创建订单，避免产生重复待支付订单。
            boolean isLocked = lock.tryLock(3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException("操作过于频繁");
            }
            return transactionTemplate.execute(status ->
                    doCreateCourseOrder(userId, courseId, reqVo.getUserCouponId())
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw BusinessException.badRequest("操作已中断，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 创建课程订单业务逻辑
     *
     * @param userId       当前用户ID
     * @param courseId     课程ID
     * @param userCouponId 用户最终选择的优惠券ID
     * @return 创建成功的订单信息
     */
    private CourseOrderBasicVo doCreateCourseOrder(Long userId, Long courseId, Long userCouponId) {
        // 1.课程必须存在并且已经上架
        Course course = courseService.getPublishedCourseById(courseId);
        // 2.免费课程无需创建订单
        if (course.getChargeType() == CourseChargeTypeEnum.FREE) {
            throw BusinessException.badRequest("免费课程无需购买");
        }
        // 3.VIP用户购买VIP免费课程时无需创建订单
        if (course.getChargeType() == CourseChargeTypeEnum.VIP_FREE) {
            LocalDateTime vipExpireTime = userService.getUserById(userId).getVipExpireTime();
            if (vipExpireTime != null && vipExpireTime.isAfter(LocalDateTime.now())) {
                throw BusinessException.badRequest("VIP用户无需购买该课程");
            }
        }

        // 4.用户已经拥有有效课程权益时，不允许重复购买
        CourseUser courseUser = courseUserService.findValidByUserIdAndCourseId(userId, courseId);
        if (Objects.nonNull(courseUser)) {
            throw BusinessException.badRequest("用户已经拥有该课程权益");
        }

        // 5.存在同一课程的待支付订单时直接返回原订单
        CourseOrder pendingOrder = courseOrderService.findPendingOrder(userId, courseId);
        if (Objects.nonNull(pendingOrder)) {
            // 轻量版暂不支持待支付订单直接换券，需要先取消原订单释放优惠券
            if (!Objects.equals(pendingOrder.getUserCouponId(), userCouponId)) {
                throw BusinessException.badRequest("该课程已有待支付订单，如需更换优惠券请先取消原订单");
            }
            return ClientCourseOrderConverter.INSTANCE.poToBasicVo(pendingOrder);
        }

        // 6.不存在待支付订单时，创建新订单
        return ClientCourseOrderConverter.INSTANCE.poToBasicVo(createOrder(course, userId, userCouponId));
    }

    /**
     * 构建并保存课程订单
     *
     * @param course       课程信息
     * @param userId       当前用户ID
     * @param userCouponId 用户最终选择的优惠券ID
     * @return 保存成功的课程订单
     */
    private CourseOrder createOrder(Course course, Long userId, Long userCouponId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusMinutes(30);
        Long orderId = IdUtil.getSnowflakeNextId();

        CourseOrder courseOrder = new CourseOrder();
        courseOrder.setId(orderId);
        courseOrder.setOrderNo("HC" + now.format(ORDER_NO_TIME_FORMATTER) + orderId);
        courseOrder.setUserId(userId);
        courseOrder.setCourseId(course.getId());
        courseOrder.setCourseTitle(course.getTitle());
        courseOrder.setCoverUrl(course.getCoverUrl());
        courseOrder.setCoursePrice(course.getPrice());
        courseOrder.setCouponName("");
        courseOrder.setDiscountAmount(0);
        courseOrder.setStatus(CourseOrderStatusEnum.PENDING_PAYMENT);
        courseOrder.setPayType(CourseOrderPayTypeEnum.UNPAID);
        courseOrder.setThirdTradeNo("");
        courseOrder.setExpireTime(expireTime);
        courseOrder.setCreateTime(now);
        courseOrder.setUpdateTime(now);
        courseOrder.setUpdatedUser(userId);
        courseOrder.setCreatedUser(userId);
        courseOrder.setDeleted(0);
        // TODO:事务
        boolean couponLocked = false;
        try {
            // 用户选择优惠券时，使用新生成的订单ID原子锁定优惠券，并保存优惠券快照
            if (userCouponId != null) {
                AvailableCouponResponse coupon = lockCoupon(orderId, userCouponId, course.getPrice(), expireTime);
                couponLocked = true;
                courseOrder.setUserCouponId(coupon.getUserCouponId());
                courseOrder.setCouponName(coupon.getCouponName());
                courseOrder.setDiscountAmount(coupon.getDiscountAmount());
            }

            // 实付金额不能小于0
            courseOrder.setPayAmount(Math.max(0, course.getPrice() - courseOrder.getDiscountAmount()));
            courseOrderService.addCourseOrder(courseOrder);
            return courseOrder;
        } catch (RuntimeException exception) {
            // 优惠券锁定成功但订单保存失败时，主动解锁优惠券作为补偿。
            if (couponLocked) {
                try {
                    unlockCoupon(orderId, userCouponId);
                } catch (RuntimeException unlockException) {
                    exception.addSuppressed(unlockException);
                }
            }
            throw exception;
        }
    }

    /**
     * 查询当前用户的课程订单详情
     *
     * @param id 订单ID
     * @return 课程订单详情
     */
    @Override
    public CourseOrderDetailVo getCourseOrderDetail(Long id) {
        CourseOrder order = courseOrderService.getCourseOrderById(id, SecurityUserHolder.getUserId());
        return ClientCourseOrderConverter.INSTANCE.poToDetailVo(order);
    }

    @Override
    public CourseOrderPayRespVo payCourseOrder(Long id) {
        // 1.先创建支付订单 还未履约
        Long userId = UserContextHolder.getUserId();
        String lockKey = "main-service:order:pay:" + userId + ":" + id;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException("退款请求处理中，请稍后重试");
            }
            // 2. 查询当前用户未过期的待支付订单
            CourseOrder pendingOrder = courseOrderService.getPendingOrderById(id, userId);
            CreatePaymentRequest innerRequest = buildCreatePaymentRequest(pendingOrder, userId);
            CreatePaymentResponse payment = requireRemoteData(payClientFeignClient.createPayment(innerRequest)
                    , "接收支付订单响应结果失败");
            return buildCourseOrderPayRespVo(pendingOrder, payment);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw BusinessException.badRequest("退款操作已中断，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public CourseOrderRefundRespVo refundCourseOrder(Long id) {
        Long userId = UserContextHolder.getRequiredUserId();
        String lockKey = "main-service:order:refund:" + userId + ":" + id;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException("退款请求处理中，请稍后重试");
            }

            CourseOrder paidOrder = courseOrderService.getCourseOrderById(id, userId);
            validateCourseOrderCanRefund(paidOrder);

            CreateRefundRequest request = buildCreateRefundRequest(paidOrder);
            CreateRefundResponse refund = requireRemoteData(payClientFeignClient.createRefund(request),
                    "创建退款单失败");
            return buildCourseOrderRefundRespVo(paidOrder, refund);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw BusinessException.badRequest("退款操作已中断，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void validateCourseOrderCanRefund(CourseOrder order) {
        if (order.getStatus() == CourseOrderStatusEnum.REFUNDED) {
            throw BusinessException.badRequest("订单已退款，请勿重复申请");
        }
        if (order.getStatus() != CourseOrderStatusEnum.PAID) {
            throw BusinessException.badRequest("只有已支付订单才允许退款");
        }
        if (order.getPayAmount() == null || order.getPayAmount() <= 0) {
            throw BusinessException.badRequest("实付金额为0的订单无需退款");
        }
    }

    private CreateRefundRequest buildCreateRefundRequest(CourseOrder order) {
        CreateRefundRequest request = new CreateRefundRequest();
        request.setBizType(1);
        request.setBizOrderId(order.getId());
        request.setBizOrderNo(order.getOrderNo());
        request.setUserId(order.getUserId());
        request.setRefundAmount(order.getPayAmount());
        request.setRefundReason("用户申请退款");
        return request;
    }

    private CourseOrderRefundRespVo buildCourseOrderRefundRespVo(CourseOrder order, CreateRefundResponse refund) {
        CourseOrderRefundRespVo respVo = new CourseOrderRefundRespVo();
        respVo.setId(order.getId());
        respVo.setOrderNo(order.getOrderNo());
        respVo.setStatus(order.getStatus());
        respVo.setRefundNo(refund.getRefundNo());
        respVo.setRefundAmount(refund.getRefundAmount());
        respVo.setRefundStatus(refund.getStatus());
        respVo.setRefundTime(LocalDateTime.now());
        return respVo;
    }

    private CourseOrderPayRespVo buildCourseOrderPayRespVo(CourseOrder pendingOrder, CreatePaymentResponse payment) {
        CourseOrderPayRespVo respVo = new CourseOrderPayRespVo();
        respVo.setId(pendingOrder.getId());
        respVo.setOrderNo(pendingOrder.getOrderNo());
        respVo.setStatus(pendingOrder.getStatus());
        respVo.setPaymentNo(payment.getPaymentNo());
        respVo.setPayAmount(payment.getPayAmount());
        respVo.setPayChannel(payment.getPayChannel());
        respVo.setPaymentStatus(payment.getStatus());
        respVo.setExpireTime(payment.getExpireTime());
        respVo.setPayTime(pendingOrder.getPayTime());
        return respVo;
    }

    private CreatePaymentRequest buildCreatePaymentRequest(CourseOrder pendingOrder, Long userId) {
        CreatePaymentRequest innerRequest = new CreatePaymentRequest();
        innerRequest.setBizType(1);
        innerRequest.setBizOrderId(pendingOrder.getId());
        innerRequest.setBizOrderNo(pendingOrder.getOrderNo());
        innerRequest.setUserId(userId);
        innerRequest.setSubject(pendingOrder.getCourseTitle());
        innerRequest.setPayAmount(pendingOrder.getPayAmount());
        // 当前阶段仅支持模拟支付，真实支付接入后由用户选择微信或支付宝渠道。
        innerRequest.setPayChannel(CourseOrderPayTypeEnum.MOCK.getCode());
        innerRequest.setExpireTime(pendingOrder.getExpireTime());
        return innerRequest;
    }

    /**
     * 创建用户课程权益
     *
     * @param order 已支付订单
     * @return 用户课程权益
     */
    private CourseUser createCourseUser(CourseOrder order) {
        LocalDateTime now = LocalDateTime.now();
        CourseUser courseUser = new CourseUser();
        courseUser.setId(IdUtil.getSnowflakeNextId());
        courseUser.setUserId(order.getUserId());
        courseUser.setCourseId(order.getCourseId());
        courseUser.setOrderId(order.getId());
        courseUser.setSourceType(CourseUserSourceTypeEnum.PURCHASE);
        courseUser.setStatus(CourseUserStatusEnum.VALID);
        courseUser.setGrantTime(now);
        courseUser.setExpireTime(null);
        courseUser.setCreatedUser(order.getUserId());
        courseUser.setCreateTime(now);
        courseUser.setUpdateTime(now);
        courseUser.setUpdatedUser(order.getUserId());
        return courseUser;
    }

    /**
     * 用户分页查询课程订单
     *
     * @param reqVo 分页查询请求对象
     * @return 课程订单分页结果
     */
    @Override
    public PageResult<CourseOrderBasicVo> getCourseOrderPageList(ClientCourseOrderPageQueryReqVo reqVo) {
        CourseOrderQuery query = ClientCourseOrderConverter.INSTANCE.reqVoToQuery(reqVo);
        query.setUserId(SecurityUserHolder.getUserId());
        IPage<CourseOrder> page = courseOrderService.pageQuery(query);
        List<CourseOrderBasicVo> records = ClientCourseOrderConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    /**
     * 用户取消课程订单
     *
     * @param id 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCourseOrder(Long id) {
        // 1.查询当前用户未过期的待支付订单
        CourseOrder pendingOrder = courseOrderService.getPendingOrderById(id, SecurityUserHolder.getUserId());
        // 2.关闭待支付订单
        courseOrderService.closePendingOrders(LocalDateTime.now(), id);
        // 3.订单使用了优惠券时，释放订单锁定的优惠券
        if (pendingOrder.getUserCouponId() != null) {
            unlockCoupon(pendingOrder.getId(), pendingOrder.getUserCouponId());
        }
    }

    /**
     * 查询课程订单可用优惠券
     *
     * @param courseId 课程ID
     * @return 可用优惠券列表
     */
    @Override
    public Result<List<CourseOrderAvailableCouponRespVo>> getAvailableList(Long courseId) {
        // 1.课程必须存在并且已经上架
        Course course = courseService.getPublishedCourseById(courseId);
        // 2.根据课程原价查询当前用户可用优惠券
        Result<List<AvailableCouponResponse>> result = couponFeignClient.getAvailableCoupons(course.getPrice());
        List<AvailableCouponResponse> availableCouponList = requireRemoteData(result, "查询用户优惠券失败");
        if (availableCouponList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<CourseOrderAvailableCouponRespVo> resultList = new ArrayList<>();
        availableCouponList.forEach(coupon -> {
            CourseOrderAvailableCouponRespVo respVo =
                    OrderAvailableCouponConverter.INSTANCE.innerToRespVo(coupon);
            respVo.setPayAmount(Math.max(0, course.getPrice() - respVo.getDiscountAmount()));
            resultList.add(respVo);
        });
        return Result.success(resultList);
    }

    /**
     * 锁定用户最终选择的优惠券
     */
    private AvailableCouponResponse lockCoupon(Long orderId, Long userCouponId, Integer orderAmount, LocalDateTime expireTime) {
        ChosenCouponReqVo reqVo = new ChosenCouponReqVo();
        reqVo.setOrderId(orderId);
        reqVo.setUserCouponId(userCouponId);
        reqVo.setOrderAmount(orderAmount);
        reqVo.setLockExpireTime(expireTime);
        return requireRemoteData(couponFeignClient.lockCoupon(reqVo), "锁定优惠券失败");
    }

    /**
     * 解锁订单占用的优惠券
     */
    private void unlockCoupon(Long orderId, Long userCouponId) {
        ChosenCouponReqVo reqVo = new ChosenCouponReqVo();
        reqVo.setOrderId(orderId);
        reqVo.setUserCouponId(userCouponId);
        requireRemoteSuccess(couponFeignClient.unlockCoupon(reqVo));
    }

    /**
     * 支付成功后核销优惠券
     */
    private void useCoupon(Long orderId, Long userCouponId) {
        ChosenCouponReqVo reqVo = new ChosenCouponReqVo();
        reqVo.setOrderId(orderId);
        reqVo.setUserCouponId(userCouponId);
        requireRemoteSuccess(couponFeignClient.useCoupon(reqVo));
    }

    /**
     * 校验远程调用结果并获取返回数据
     */
    private <T> T requireRemoteData(Result<T> result, String detail) {
        if (result == null || result.getCode() != 200 || result.getData() == null) {
            String message = result == null ? "远程服务无响应" : result.getMsg();
            throw new BusinessException(detail + ":" + message);
        }
        return result.getData();
    }

    /**
     * 校验不需要返回数据的远程调用结果
     */
    private void requireRemoteSuccess(Result<?> result) {
        if (result == null || result.getCode() != 200) {
            String message = result == null ? "远程服务无响应" : result.getMsg();
            throw new BusinessException("核销优惠券失败: " + message);
        }
    }
}
