package com.haoclass.coupon.application;

import com.haoclass.common.result.PageResult;
import com.haoclass.coupon.interfaces.vo.client.request.ClientMyCouponPageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientMyCouponBasicVo;

/**
 * C端我的优惠券应用服务
 */
public interface ClientMyCouponApplicationService {

    /**
     * 分页查询当前用户的优惠券
     *
     * @param reqVo 分页查询条件
     * @return 我的优惠券分页结果
     */
    PageResult<ClientMyCouponBasicVo> getMyCouponPageList(ClientMyCouponPageQueryReqVo reqVo);
}
