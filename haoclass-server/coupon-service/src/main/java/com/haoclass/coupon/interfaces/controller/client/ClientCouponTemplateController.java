package com.haoclass.coupon.interfaces.controller.client;

import com.haoclass.common.result.Result;
import com.haoclass.coupon.application.CouponTemplateApplicationService;
import com.haoclass.coupon.interfaces.vo.client.response.ClientCouponTemplateBasicVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * c端-优惠券服务-优惠券模板
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/client/templates")
public class ClientCouponTemplateController {

    private final CouponTemplateApplicationService couponTemplateApplicationService;

    /**
     * 查询可领取优惠券
     *
     * @return
     */
    @GetMapping
    public Result<List<ClientCouponTemplateBasicVo>> getList() {
        log.info("优惠券模板可领取查询");
        return Result.success(couponTemplateApplicationService.getClientCouponList());
    }

    /**
     * 用户领取优惠券
     *
     * @return
     */
    @PostMapping("{id}/receive")
    public Result<Void> receiveCouponTemplate(@PathVariable("id") Long id) {
        log.info("用户领取优惠券模板");
        couponTemplateApplicationService.receiveCouponTemplate(id);
        return Result.success();
    }
}
