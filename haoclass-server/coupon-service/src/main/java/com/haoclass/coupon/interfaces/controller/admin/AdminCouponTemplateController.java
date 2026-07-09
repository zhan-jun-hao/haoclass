package com.haoclass.coupon.interfaces.controller.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.coupon.application.CouponTemplateApplicationService;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplatePageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateUpdateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateBasicVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * b端-优惠券服务-优惠券模板
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/admin/templates")
public class AdminCouponTemplateController {

    private final CouponTemplateApplicationService couponTemplateApplicationService;

    /**
     * 分页查询优惠券模板
     *
     * @param reqVo
     * @return
     */
    @GetMapping
    public Result<PageResult<AdminCouponTemplateBasicVo>> getPageList(@Valid AdminCouponTemplatePageQueryReqVo reqVo) {
        log.info("优惠券模板分页查询: {}", reqVo);
        return Result.success(couponTemplateApplicationService.getCouponTemplatePageList(reqVo));
    }

    /**
     * 查询优惠券模板详情
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<AdminCouponTemplateDetailVo> getDetail(@PathVariable("id") Long id) {
        log.info("优惠券模板详情查询: {}", id);
        return Result.success(couponTemplateApplicationService.getCouponTemplateDetail(id));
    }

    /**
     * 新增优惠券模板
     *
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<Void> addCouponTemplate(@RequestBody @Valid AdminCouponTemplateReqVo reqVo) {
        log.info("新增优惠券模板: {}", reqVo);
        couponTemplateApplicationService.addCouponTemplate(reqVo);
        return Result.success();
    }

    /**
     * 修改优惠券模板
     *
     * @param id
     * @param reqVo
     * @return
     */
    @PutMapping("{id}")
    public Result<Void> updateCouponTemplate(@PathVariable("id") Long id, @RequestBody @Valid AdminCouponTemplateUpdateReqVo reqVo) {
        log.info("优惠券模板修改: id={}, reqVo={}", id, reqVo);
        couponTemplateApplicationService.modifyCouponTemplate(id, reqVo);
        return Result.success();
    }

    /**
     * 删除优惠券
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteCouponTemplate(@PathVariable("id") Long id) {
        log.info("优惠券模板删除: {}", id);
        couponTemplateApplicationService.removeCouponTemplate(id);
        return Result.success();
    }

    /**
     * 发放优惠券
     *
     * @param id
     * @return
     */
    @PutMapping("{id}/publish")
    public Result<Void> publishCouponTemplate(@PathVariable("id") Long id) {
        log.info("优惠券模板发放: {}", id);
        couponTemplateApplicationService.publishCouponTemplate(id);
        return Result.success();
    }

    /**
     * 停用优惠券
     *
     * @param id
     * @return
     */
    @PutMapping("{id}/stop")
    public Result<Void> stopCouponTemplate(@PathVariable("{id}") Long id) {
        log.info("优惠券模板停用: {}", id);
        couponTemplateApplicationService.stopCouponTemplate(id);
        return Result.success();
    }
}
