package com.haoclass.coupon.interfaces.controller.inner;

import com.haoclass.common.result.Result;
import com.haoclass.coupon.api.dto.request.ChosenCouponReqVo;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import com.haoclass.coupon.application.service.inner.InnerUserCouponApplicationService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户优惠券内部调用接口
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/inner/user-coupons")
public class InnerUserCouponController {

    private final InnerUserCouponApplicationService innerUserCouponApplicationService;

    /**
     * 查询当前用户的订单可用优惠券
     */
    @GetMapping("/available")
    public Result<List<AvailableCouponResponse>> getAvailableCoupons(@RequestParam("orderAmount")
                                                                     @Min(value = 0, message = "订单金额不能小于0") Integer orderAmount) {
        return Result.success(innerUserCouponApplicationService.getAvailableCouponList(orderAmount));
    }

    /**
     * 订单锁定优惠券
     *
     * @param reqVo
     * @return
     */
    @PutMapping("/lock")
    public Result<AvailableCouponResponse> lockCoupon(@RequestBody ChosenCouponReqVo reqVo) {
        return Result.success(innerUserCouponApplicationService.lockCoupon(reqVo));
    }

    /**
     * 订单解锁优惠券
     *
     * @param reqVo
     * @return
     */
    @PutMapping("/unlock")
    public Result<Void> unlockCoupon(@RequestBody ChosenCouponReqVo reqVo) {
        innerUserCouponApplicationService.unlockCoupon(reqVo);
        return Result.success();
    }

    /**
     * 批量解锁优惠券
     *
     * @return
     */
    @PutMapping("/return/coupons")
    public Result<Void> returnCoupons(@RequestBody Set<Long> ids) {
        innerUserCouponApplicationService.returnCoupons(ids);
        return Result.success();
    }

    /**
     * 支付成功后核销优惠券
     */
    @PutMapping("/use")
    public Result<Void> useCoupon(@RequestBody ChosenCouponReqVo reqVo) {
        innerUserCouponApplicationService.useCoupon(reqVo);
        return Result.success();
    }

    /**
     * 查询可用优惠券
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AvailableCouponResponse> getAvailableCoupon(@PathVariable("id") Long id) {
        return Result.success(innerUserCouponApplicationService.getAvailableCoupon(id));
    }
}
