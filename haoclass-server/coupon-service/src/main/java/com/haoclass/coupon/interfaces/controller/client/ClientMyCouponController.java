package com.haoclass.coupon.interfaces.controller.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.coupon.application.ClientMyCouponApplicationService;
import com.haoclass.coupon.interfaces.vo.client.request.ClientMyCouponPageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientMyCouponBasicVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * c端-优惠券服务-我的优惠券
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/client/my-coupons")
public class ClientMyCouponController {

    private final ClientMyCouponApplicationService clientMyCouponApplicationService;

    /**
     * 分页查询我的优惠券
     *
     * @return
     */
    @GetMapping
    public Result<PageResult<ClientMyCouponBasicVo>> getPageList(@Valid ClientMyCouponPageQueryReqVo reqVo) {
        log.info("分页查询我的优惠券: {}", reqVo);
        return Result.success(clientMyCouponApplicationService.getMyCouponPageList(reqVo));
    }

}
