package com.haoclass.coupon.application;

import com.haoclass.common.result.PageResult;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplatePageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplateUpdateReqVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateBasicVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateDetailVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientCouponTemplateBasicVo;

import java.util.List;

public interface CouponTemplateApplicationService {

    /**
     * 分页查询优惠券模板
     *
     * @param reqVo
     * @return
     */
    PageResult<AdminCouponTemplateBasicVo> getCouponTemplatePageList(AdminCouponTemplatePageQueryReqVo reqVo);

    /**
     * 查询优惠券模板详情
     *
     * @param id
     * @return
     */
    AdminCouponTemplateDetailVo getCouponTemplateDetail(Long id);

    /**
     * 添加优惠券模板
     *
     * @param reqVo
     */
    void addCouponTemplate(AdminCouponTemplateReqVo reqVo);

    /**
     * 修改优惠券模板
     *
     * @param id
     * @param reqVo
     */
    void modifyCouponTemplate(Long id, AdminCouponTemplateUpdateReqVo reqVo);

    /**
     * 删除优惠券
     *
     * @param id
     */
    void removeCouponTemplate(Long id);

    /**
     * 发放优惠券
     *
     * @param id
     */
    void publishCouponTemplate(Long id);

    /**
     * 停用优惠券
     *
     * @param id
     */
    void stopCouponTemplate(Long id);

    /**
     * 获取用户可领取的优惠券
     *
     * @return
     */
    List<ClientCouponTemplateBasicVo> getClientCouponList();

    /**
     * 用户领取优惠券
     */
    void receiveCouponTemplate(Long id);
}
