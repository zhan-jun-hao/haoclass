package com.haoclass.main.interfaces.vo.order.client.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoclass.main.infrastructure.common.enums.CourseOrderPayTypeEnum;
import com.haoclass.main.infrastructure.common.enums.CourseOrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseOrderDetailVo {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程标题快照
     */
    private String courseTitle;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 课程原价，单位：分
     */
    private Integer coursePrice;

    /**
     * 使用的用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 优惠券名称快照
     */
    private String couponName;

    /**
     * 优惠金额，单位：分
     */
    private Integer discountAmount;

    /**
     * 实付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 订单状态
     */
    private CourseOrderStatusEnum status;

    /**
     * 支付方式：0未支付 1微信 2支付宝 3模拟支付
     */
    private CourseOrderPayTypeEnum payType;

    /**
     * 第三方支付流水号
     */
    private String thirdTradeNo;

    /**
     * 订单过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    /**
     * 退款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
