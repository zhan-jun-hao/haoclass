package com.haoclass.coupon.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.coupon.domain.model.query.UserCouponQuery;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户优惠券领域服务。
 */
public interface UserCouponService extends IService<UserCoupon> {

    /**
     * 查询用户已经领取的记录
     *
     * @return
     */
    List<UserCoupon> findByUserId(Long userId);

    /**
     * 查询用户是否已经领取过这个优惠券
     *
     * @param couponTemplateId
     * @param userId
     * @return
     */
    UserCoupon findByTemplateIdAndUserId(Long couponTemplateId, Long userId);

    /**
     * 分页查询指定用户的优惠券
     *
     * @param userId 用户ID
     * @param query  分页查询条件
     * @return 用户优惠券分页结果
     */
    IPage<UserCoupon> pageByUserId(Long userId, UserCouponQuery query);

    /**
     * 将已过期但仍处于未使用状态的优惠券更新为已过期
     *
     * @param userId 用户ID
     */
    void expireUnusedCoupons(Long userId);

    /**
     * 保存用户领取的优惠券
     *
     * @param userCoupon 用户优惠券
     */
    void saveUserCoupon(UserCoupon userCoupon);

    /**
     * 查询可领取的优惠券
     *
     * @param userId
     * @param orderAmount
     * @return
     */
    List<UserCoupon> findAvailableCoupons(Long userId, Integer orderAmount);

    /**
     * 查询用户选择的优惠券
     *
     * @param userId
     * @param userCouponId
     * @return
     */
    UserCoupon getChosenCoupon(Long userId, Long userCouponId, Long orderId);

    /**
     * 订单锁定优惠券
     *
     * @param userId
     * @param userCouponId
     * @param orderId
     */
    UserCoupon lockCoupon(Long userId, Long userCouponId, Long orderId, Integer orderAmount, LocalDateTime lockExpireTime);

    /**
     * 订单取消优惠券
     *
     * @param userId
     * @param userCouponId
     * @param orderId
     */
    void unlockCoupon(Long userId, Long userCouponId, Long orderId);


    /**
     * 批量解锁优惠券
     *
     * @param ids
     */
    void returnCoupons(Set<Long> ids);

    /**
     * 支付成功后核销优惠券
     *
     * @param userId       用户ID
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     */
    void useCoupon(Long userId, Long userCouponId, Long orderId);

    /**
     * 尝试核销优惠券
     *
     * @return 是否成功完成状态更新
     */
    boolean tryUseCoupon(Long userId, Long userCouponId, Long orderId);

    /**
     * 释放锁定已过期的优惠券
     *
     * @param now 当前时间
     */
    void releaseExpiredLockedCoupons(java.time.LocalDateTime now);

    /**
     * 通过id查询已使用优惠券
     *
     * @param userCouponId
     * @param userId
     * @param orderId
     * @return
     */
    UserCoupon findUsedCouponById(Long userCouponId, Long userId, Long orderId);
}
