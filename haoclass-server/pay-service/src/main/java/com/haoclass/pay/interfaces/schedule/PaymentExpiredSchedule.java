package com.haoclass.pay.interfaces.schedule;

import com.haoclass.pay.application.service.inner.InnerOrderPayApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentExpiredSchedule {

    private final RedissonClient redissonClient;
    private final InnerOrderPayApplicationService innerOrderPayApplicationService;

    @Scheduled(cron = "0 * * * * ?")
    public void closeExpiredPendingPaymentOrders() {
        String lockKey = "pay-service:order:schedule:close-expired-paymentorders";
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 60, TimeUnit.SECONDS);
            if(!isLocked) {
                return;
            }
            int batchSize = 5000;
            innerOrderPayApplicationService.closeExpiredPendingOrders(batchSize);
        } catch (InterruptedException e) {
            // 标记该线程已经中断
            Thread.currentThread().interrupt();
            log.info("定时任务已中断");
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


}
