package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_order")
public class CourseOrder extends BaseEntity {

    /**
     * 订单号，对外展示和支付使用
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 课程ID
     */
    @TableField("courseId")
    private Long courseId;

    /**
     * 课程标题快照
     */
    @TableField("courseTitle")
    private String courseTitle;

    /**
     * 课程封面快照
     */
    @TableField("coverUrl")
    private String coverUrl;

    /**
     * 课程原价，单位：分
     */
    @TableField("coursePrice")
    private Integer coursePrice;

    /**
     * 使用的用户优惠券ID
     */
    @TableField("userCouponId")
    private Long userCouponId;

    /**
     * 优惠券名称快照
     */
    @TableField("couponName")
    private String couponName;

    /**
     * 优惠金额，单位：分
     */
    @TableField("discountAmount")
    private Integer discountAmount;

    /**
     * 实付金额，单位：分
     */
    @TableField("payAmount")
    private Integer payAmount;

    /**
     * 订单状态：
     * 0待支付 1已支付 2已取消 3已退款 4已关闭
     */
    @TableField("status")
    private CourseOrderStatusEnum status;

    /**
     * 支付方式：
     * 0未支付 1微信 2支付宝 3模拟支付
     */
    @TableField("payType")
    private CourseOrderPayTypeEnum payType;

    /**
     * 第三方支付流水号
     */
    @TableField("thirdTradeNo")
    private String thirdTradeNo;

    /**
     * 订单过期时间
     */
    @TableField("expireTime")
    private LocalDateTime expireTime;

    /**
     * 支付时间
     */
    @TableField("payTime")
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    @TableField("cancelTime")
    private LocalDateTime cancelTime;

    /**
     * 退款时间
     */
    @TableField("refundTime")
    private LocalDateTime refundTime;
}
