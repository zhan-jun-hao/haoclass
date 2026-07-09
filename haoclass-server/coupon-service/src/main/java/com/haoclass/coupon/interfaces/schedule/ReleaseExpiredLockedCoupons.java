package com.haoclass.coupon.interfaces.schedule;

import com.haoclass.coupon.application.service.inner.InnerUserCouponApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 释放锁定已过期的优惠券
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseExpiredLockedCoupons {

    private final InnerUserCouponApplicationService innerUserCouponApplicationService;
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 * * * * ?")
    public void releaseExpiredLockedCoupons() {
        RLock lock = redissonClient.getLock("coupon-service:schedule:release-expired-locked-coupons");
        try {
            if (!lock.tryLock(0, 60, TimeUnit.SECONDS)) {
                return;
            }
            innerUserCouponApplicationService.releaseExpiredLockedCoupons(LocalDateTime.now());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("释放过期锁券任务已中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
