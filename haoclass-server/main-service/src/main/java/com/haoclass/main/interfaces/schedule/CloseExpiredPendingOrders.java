package com.haoclass.main.interfaces.schedule;

import com.haoclass.coupon.api.client.CouponFeignClient;
import com.haoclass.main.application.service.inner.InnerCourseOrderApplicationService;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloseExpiredPendingOrders {

    private final InnerCourseOrderApplicationService innerCourseOrderApplicationService;
    private final RedissonClient redissonClient;
    private final CouponFeignClient couponFeignClient;

    @Scheduled(cron = "0 * * * * ?")
    public void closeExpiredPendingOrders() {
        String lockKey = "main-service:order:schedule:close-expired-orders";
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 60,TimeUnit.SECONDS);
            if(!isLocked) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            // 1.批量查询过期订单
            List<CourseOrder> expiredPendingOrders = innerCourseOrderApplicationService.findExpiredPendingOrders(now);
            if(expiredPendingOrders.isEmpty()) {
                return;
            }
            // 2.取消订单
            Set<Long> expiredIds = expiredPendingOrders.stream().map(CourseOrder::getId).collect(Collectors.toSet());
            innerCourseOrderApplicationService.closeExpiredPendingOrders(expiredIds, now);
            // 3.归还优惠券
            Set<Long> userCouponIds = expiredPendingOrders.stream()
                    .map(CourseOrder::getUserCouponId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if(!userCouponIds.isEmpty()) {
                couponFeignClient.returnCoupons(userCouponIds);
            }

        } catch (InterruptedException e) {
            // 标记该线程已经中断
            Thread.currentThread().interrupt();
            log.warn("关闭过期订单定时任务已中断", e);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
