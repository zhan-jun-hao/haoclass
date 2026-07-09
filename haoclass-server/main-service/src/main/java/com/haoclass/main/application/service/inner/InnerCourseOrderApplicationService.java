package com.haoclass.main.application.service.inner;

import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.pay.api.dto.request.PaymentClosedRequest;
import com.haoclass.pay.api.dto.request.PaymentRefundSuccessRequest;
import com.haoclass.pay.api.dto.request.PaymentSuccessRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface InnerCourseOrderApplicationService {

    /**
     * 更新待支付订单为已关闭
     *
     * @param now
     */
    void closeExpiredPendingOrders(Set<Long> ids, LocalDateTime now);

    /**
     * 批量查询待支付订单
     *
     * @param now
     * @return
     */
    List<CourseOrder> findExpiredPendingOrders(LocalDateTime now);

    /**
     * 处理支付成功后的课程订单履约
     *
     * @param request 支付成功通知
     */
    void handlePaymentSuccess(PaymentSuccessRequest request);

    /**
     * 处理支付关闭后的课程订单关闭
     *
     * @param request 支付关闭通知
     */
    void handlePaymentClosed(PaymentClosedRequest request);

    /**
     * 处理退款成功后的课程订单履约
     *
     * @param request 退款成功通知
     */
    void handleRefundSuccess(PaymentRefundSuccessRequest request);

}
