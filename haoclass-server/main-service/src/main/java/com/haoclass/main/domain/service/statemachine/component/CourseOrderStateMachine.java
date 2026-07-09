package com.haoclass.main.domain.service.statemachine.component;

import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import com.haoclass.main.domain.service.statemachine.context.CourseOrderContext;
import com.haoclass.main.domain.service.statemachine.support.CourseOrderEventEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 课程订单状态机
 */
@Slf4j
@Configuration
public class CourseOrderStateMachine {

    public static final String MACHINE_ID = "course_order_state_machine";

    @Bean(MACHINE_ID)
    public StateMachine<CourseOrderStatusEnum, CourseOrderEventEnum, CourseOrderContext> buildCourseOrderStateMachine() {

        StateMachineBuilder<CourseOrderStatusEnum, CourseOrderEventEnum, CourseOrderContext> builder =
                StateMachineBuilderFactory.create();

        // 待支付 -> 已支付
        builder.externalTransition()
                .from(CourseOrderStatusEnum.PENDING_PAYMENT)
                .to(CourseOrderStatusEnum.PAID)
                .on(CourseOrderEventEnum.PAYMENT_SUCCESS)
                // 只有当前订单状态为待支付时才会转化为已支付
                .when(context -> context.getCurrentStatus() == CourseOrderStatusEnum.PENDING_PAYMENT)
                // 状态迁移成功后做什么动作
                .perform((from, to, event,
                          context) ->
                        log.info("订单状态可以流转为: 支付成功，订单ID：{}，状态：{} -> {}", context.getId(), from, to)
                );

        // 待支付 -> 已取消
        builder.externalTransition()
                .from(CourseOrderStatusEnum.PENDING_PAYMENT)
                .to(CourseOrderStatusEnum.CANCELLED)
                .on(CourseOrderEventEnum.USER_CANCEL)
                .when(context -> context.getCurrentStatus() == CourseOrderStatusEnum.PENDING_PAYMENT)
                .perform((from, to, event,
                          context) ->
                        log.info("订单状态可以流转为: 取消订单，订单ID：{}，状态：{} -> {}", context.getId(), from, to)
                );

        // 待支付 -> 已关闭
        builder.externalTransition()
                .from(CourseOrderStatusEnum.PENDING_PAYMENT)
                .to(CourseOrderStatusEnum.CLOSED)
                .on(CourseOrderEventEnum.TIMEOUT_CLOSE)
                .when(context -> context.getCurrentStatus() == CourseOrderStatusEnum.PENDING_PAYMENT)
                .perform((from, to, event,
                          context) ->
                        log.info("订单状态可以流转为: 超时关闭，订单ID：{}，状态：{} -> {}", context.getId(), from, to)
                );

        // 已支付 -> 已退款
        builder.externalTransition()
                .from(CourseOrderStatusEnum.PAID)
                .to(CourseOrderStatusEnum.REFUNDED)
                .on(CourseOrderEventEnum.REFUND_SUCCESS)
                .when(context -> context.getCurrentStatus() == CourseOrderStatusEnum.PAID)
                .perform((from, to, event,
                          context) ->
                    log.info("订单状态可以流转为: 退款成功，订单ID：{}，状态：{} -> {}", context.getId(), from, to)
                );

        StateMachine<CourseOrderStatusEnum, CourseOrderEventEnum, CourseOrderContext> stateMachine =
                builder.build(MACHINE_ID);

        log.info("课程订单状态机初始化完成");
        log.info(stateMachine.generatePlantUML());

        return stateMachine;
    }
}
