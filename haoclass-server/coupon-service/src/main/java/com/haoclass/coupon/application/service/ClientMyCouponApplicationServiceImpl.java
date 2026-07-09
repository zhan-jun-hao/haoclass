package com.haoclass.coupon.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.coupon.application.ClientMyCouponApplicationService;
import com.haoclass.coupon.application.converter.client.ClientMyCouponConverter;
import com.haoclass.coupon.domain.model.query.UserCouponQuery;
import com.haoclass.coupon.domain.service.UserCouponService;
import com.haoclass.coupon.infrastructure.persistence.po.UserCoupon;
import com.haoclass.coupon.interfaces.vo.client.request.ClientMyCouponPageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.client.response.ClientMyCouponBasicVo;
import com.haoclass.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * C端我的优惠券应用服务实现
 */
@Service
@RequiredArgsConstructor
public class ClientMyCouponApplicationServiceImpl implements ClientMyCouponApplicationService {

    private final UserCouponService userCouponService;

    @Override
    public PageResult<ClientMyCouponBasicVo> getMyCouponPageList(ClientMyCouponPageQueryReqVo reqVo) {
        Long userId = UserContextHolder.getRequiredUserId();
        userCouponService.expireUnusedCoupons(userId);
        UserCouponQuery query = ClientMyCouponConverter.INSTANCE.reqVoToQuery(reqVo);
        IPage<UserCoupon> page = userCouponService.pageByUserId(userId, query);
        return PageResult.success(page, ClientMyCouponConverter.INSTANCE.poToBasicVo(page.getRecords()));
    }
}
