package com.haoclass.coupon.api.client;

import com.haoclass.common.result.Result;
import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 优惠券服务远程调用接口
 */
@FeignClient(name = "coupon-service", path = "/api/coupon/inner/user-coupons")
public interface CouponFeignClient {

    /**
     * 查询当前用户可用于指定订单金额的优惠券
     *
     * @param orderAmount 订单原始金额，单位：分
     * @return 可用优惠券列表
     */
    @GetMapping("/available")
    Result<List<AvailableCouponResponse>> getAvailableCoupons(@RequestParam("orderAmount") Integer orderAmount);

    /**
     * 订单锁定优惠券 UNUSED -> LOCKED
     *
     * @param reqVo
     * @return
     */
    @PutMapping("/lock")
    Result<AvailableCouponResponse> lockCoupon(@RequestBody ChosenCouponReqVo reqVo);

    /**
     * 订单解锁优惠券
     *
     * @param reqVo
     * @return
     */
    @PutMapping("/unlock")
    Result<Void> unlockCoupon(@RequestBody ChosenCouponReqVo reqVo);

    /**
     * 批量解锁优惠券
     *
     * @return
     */
    @PutMapping("/return/coupons")
    Result<Void> returnCoupons(@RequestBody Set<Long> ids);

    /**
     * 支付成功后优惠券 LOCKED -> USED
     *
     * @param reqVo
     * @return
     */
    @PutMapping("/use")
    Result<Void> useCoupon(@RequestBody ChosenCouponReqVo reqVo);

    /**
     * 查询可用优惠券
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<AvailableCouponResponse> getAvailableCoupon(@PathVariable("id") Long id);
}
