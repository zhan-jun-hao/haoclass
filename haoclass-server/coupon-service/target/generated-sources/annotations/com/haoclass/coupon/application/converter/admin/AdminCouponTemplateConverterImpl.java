package com.haoclass.coupon.application.converter.admin;

import com.haoclass.coupon.domain.model.query.CouponTemplateQuery;
import com.haoclass.coupon.infrastructure.persistence.po.CouponTemplate;
import com.haoclass.coupon.interfaces.vo.admin.request.AdminCouponTemplatePageQueryReqVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateBasicVo;
import com.haoclass.coupon.interfaces.vo.admin.response.AdminCouponTemplateDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T12:18:38+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminCouponTemplateConverterImpl implements AdminCouponTemplateConverter {

    @Override
    public CouponTemplateQuery pageReqVoToQuery(AdminCouponTemplatePageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CouponTemplateQuery couponTemplateQuery = new CouponTemplateQuery();

        couponTemplateQuery.setCurrent( reqVo.getCurrent() );
        couponTemplateQuery.setSize( reqVo.getSize() );
        couponTemplateQuery.setCouponName( reqVo.getCouponName() );
        couponTemplateQuery.setStatus( reqVo.getStatus() );
        couponTemplateQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        couponTemplateQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );

        return couponTemplateQuery;
    }

    @Override
    public List<AdminCouponTemplateBasicVo> poToBasicVo(List<CouponTemplate> poList) {
        if ( poList == null ) {
            return null;
        }

        List<AdminCouponTemplateBasicVo> list = new ArrayList<AdminCouponTemplateBasicVo>( poList.size() );
        for ( CouponTemplate couponTemplate : poList ) {
            list.add( couponTemplateToAdminCouponTemplateBasicVo( couponTemplate ) );
        }

        return list;
    }

    @Override
    public AdminCouponTemplateDetailVo poToDetailVo(CouponTemplate po) {
        if ( po == null ) {
            return null;
        }

        AdminCouponTemplateDetailVo adminCouponTemplateDetailVo = new AdminCouponTemplateDetailVo();

        adminCouponTemplateDetailVo.setId( po.getId() );
        adminCouponTemplateDetailVo.setCouponName( po.getCouponName() );
        adminCouponTemplateDetailVo.setDescription( po.getDescription() );
        adminCouponTemplateDetailVo.setThresholdAmount( po.getThresholdAmount() );
        adminCouponTemplateDetailVo.setDiscountAmount( po.getDiscountAmount() );
        adminCouponTemplateDetailVo.setTotalStock( po.getTotalStock() );
        adminCouponTemplateDetailVo.setReceivedCount( po.getReceivedCount() );
        adminCouponTemplateDetailVo.setReceiveStartTime( po.getReceiveStartTime() );
        adminCouponTemplateDetailVo.setReceiveEndTime( po.getReceiveEndTime() );
        adminCouponTemplateDetailVo.setValidStartTime( po.getValidStartTime() );
        adminCouponTemplateDetailVo.setValidEndTime( po.getValidEndTime() );
        adminCouponTemplateDetailVo.setStatus( po.getStatus() );
        adminCouponTemplateDetailVo.setCreatedUser( po.getCreatedUser() );
        adminCouponTemplateDetailVo.setUpdatedUser( po.getUpdatedUser() );
        adminCouponTemplateDetailVo.setCreateTime( po.getCreateTime() );
        adminCouponTemplateDetailVo.setUpdateTime( po.getUpdateTime() );

        return adminCouponTemplateDetailVo;
    }

    protected AdminCouponTemplateBasicVo couponTemplateToAdminCouponTemplateBasicVo(CouponTemplate couponTemplate) {
        if ( couponTemplate == null ) {
            return null;
        }

        AdminCouponTemplateBasicVo adminCouponTemplateBasicVo = new AdminCouponTemplateBasicVo();

        adminCouponTemplateBasicVo.setId( couponTemplate.getId() );
        adminCouponTemplateBasicVo.setCouponName( couponTemplate.getCouponName() );
        adminCouponTemplateBasicVo.setThresholdAmount( couponTemplate.getThresholdAmount() );
        adminCouponTemplateBasicVo.setDiscountAmount( couponTemplate.getDiscountAmount() );
        adminCouponTemplateBasicVo.setTotalStock( couponTemplate.getTotalStock() );
        adminCouponTemplateBasicVo.setReceivedCount( couponTemplate.getReceivedCount() );
        adminCouponTemplateBasicVo.setReceiveStartTime( couponTemplate.getReceiveStartTime() );
        adminCouponTemplateBasicVo.setReceiveEndTime( couponTemplate.getReceiveEndTime() );
        adminCouponTemplateBasicVo.setValidEndTime( couponTemplate.getValidEndTime() );
        adminCouponTemplateBasicVo.setStatus( couponTemplate.getStatus() );
        adminCouponTemplateBasicVo.setCreateTime( couponTemplate.getCreateTime() );
        adminCouponTemplateBasicVo.setUpdateTime( couponTemplate.getUpdateTime() );

        return adminCouponTemplateBasicVo;
    }
}
