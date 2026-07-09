package com.haoclass.pay.interfaces.schedule;

import com.haoclass.pay.domain.service.MqMessageService;
import com.haoclass.pay.infrastructure.mq.MqMessagePublisher;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 本地消息表-消息发送
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendPendingMessageSchedule {

    private final RedissonClient redissonClient;
    private final MqMessageService mqMessageService;
    private final MqMessagePublisher mqMessagePublisher;

    @Scheduled(cron = "0 * * * * ?")
    public void sendPendingMessages() {
        String lockKey = "pay-service:order:schedule:send-pending";
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(0, 60, TimeUnit.SECONDS);
            if(!isLocked) {
                return;
            }
            sendMessages();
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

    private void sendMessages() {
        List<MqMessage> messageList = mqMessageService.findPendingMessageList();
        if (messageList.isEmpty()) {
            return;
        }
        messageList.forEach(mqMessagePublisher::send);
    }
}
