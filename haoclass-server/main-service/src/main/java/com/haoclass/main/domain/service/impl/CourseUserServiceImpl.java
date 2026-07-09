package com.haoclass.main.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.service.CourseUserService;
import com.haoclass.main.infrastructure.common.enums.CourseUserSourceTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseUserStatusEnum;
import com.haoclass.main.infrastructure.persistence.mapper.CourseUserMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseUserServiceImpl extends ServiceImpl<CourseUserMapper, CourseUser> implements CourseUserService {

    @Override
    public CourseUser findValidByUserIdAndCourseId(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseUser> wrapper = Wrappers.lambdaQuery(CourseUser.class)
                .eq(CourseUser::getUserId, userId)
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getDeleted, 0)
                .eq(CourseUser::getStatus, CourseUserStatusEnum.VALID)
                .and(condition -> condition
                        .isNull(CourseUser::getExpireTime)
                        .or()
                        .gt(CourseUser::getExpireTime, LocalDateTime.now()))
                .orderByDesc(CourseUser::getGrantTime);

        return this.getOne(wrapper, false);
    }

    @Override
    public CourseUser findByUserIdAndCourseId(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseUser> wrapper = Wrappers.lambdaQuery(CourseUser.class)
                .eq(CourseUser::getUserId, userId)
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getDeleted, 0);

        return this.getOne(wrapper, false);
    }

    @Override
    public void addCourseUser(CourseUser courseUser) {
        boolean saved = this.save(courseUser);
        if (!saved) {
            throw BusinessException.badRequest("创建课程权益失败");
        }
    }

    @Override
    public void updateCourseUser(CourseUser courseUser) {
        boolean updated = this.updateById(courseUser);
        if (!updated) {
            throw BusinessException.badRequest("更新课程权益失败");
        }
    }

    @Override
    public List<CourseUser> listValidByUserId(Long userId) {
        LambdaQueryWrapper<CourseUser> wrapper = Wrappers.lambdaQuery(CourseUser.class)
                .eq(CourseUser::getUserId, userId)
                .eq(CourseUser::getDeleted, 0)
                .eq(CourseUser::getStatus, CourseUserStatusEnum.VALID)
                .and(condition -> condition
                        .isNull(CourseUser::getExpireTime)
                        .or()
                        .gt(CourseUser::getExpireTime, LocalDateTime.now()));

        return this.list(wrapper);
    }

    @Override
    public void refundCourseUser(Long userId, Long courseId, Long orderId, LocalDateTime refundTime) {
        LambdaUpdateWrapper<CourseUser> wrapper = Wrappers.lambdaUpdate(CourseUser.class)
                .eq(CourseUser::getDeleted, 0)
                .eq(CourseUser::getUserId, userId)
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getOrderId, orderId)
                .eq(CourseUser::getSourceType, CourseUserSourceTypeEnum.PURCHASE)
                .eq(CourseUser::getStatus, CourseUserStatusEnum.VALID)
                .set(CourseUser::getStatus, CourseUserStatusEnum.REFUND_INVALID)
                .set(CourseUser::getExpireTime, refundTime)
                .set(CourseUser::getUpdateTime, refundTime)
                .set(CourseUser::getUpdatedUser, userId);

        boolean updated = this.update(wrapper);
        if (updated) {
            return;
        }

        CourseUser courseUser = findByUserIdAndCourseId(userId, courseId);
        if (courseUser != null && courseUser.getStatus() == CourseUserStatusEnum.REFUND_INVALID) {
            return;
        }
        throw BusinessException.badRequest("回收课程权益失败");
    }
}
