package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.interfaces.vo.order.admin.request.AdminCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.admin.response.AdminCourseOrderDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminCourseOrderConverterImpl implements AdminCourseOrderConverter {

    @Override
    public List<AdminCourseOrderBasicVo> poToBasicVo(List<CourseOrder> courseOrderList) {
        if ( courseOrderList == null ) {
            return null;
        }

        List<AdminCourseOrderBasicVo> list = new ArrayList<AdminCourseOrderBasicVo>( courseOrderList.size() );
        for ( CourseOrder courseOrder : courseOrderList ) {
            list.add( courseOrderToAdminCourseOrderBasicVo( courseOrder ) );
        }

        return list;
    }

    @Override
    public AdminCourseOrderDetailVo poToDetailVo(CourseOrder courseOrder) {
        if ( courseOrder == null ) {
            return null;
        }

        AdminCourseOrderDetailVo adminCourseOrderDetailVo = new AdminCourseOrderDetailVo();

        adminCourseOrderDetailVo.setId( courseOrder.getId() );
        adminCourseOrderDetailVo.setOrderNo( courseOrder.getOrderNo() );
        adminCourseOrderDetailVo.setUserId( courseOrder.getUserId() );
        adminCourseOrderDetailVo.setCourseId( courseOrder.getCourseId() );
        adminCourseOrderDetailVo.setCourseTitle( courseOrder.getCourseTitle() );
        adminCourseOrderDetailVo.setCoverUrl( courseOrder.getCoverUrl() );
        adminCourseOrderDetailVo.setCoursePrice( courseOrder.getCoursePrice() );
        adminCourseOrderDetailVo.setUserCouponId( courseOrder.getUserCouponId() );
        adminCourseOrderDetailVo.setCouponName( courseOrder.getCouponName() );
        adminCourseOrderDetailVo.setDiscountAmount( courseOrder.getDiscountAmount() );
        adminCourseOrderDetailVo.setPayAmount( courseOrder.getPayAmount() );
        adminCourseOrderDetailVo.setStatus( courseOrder.getStatus() );
        adminCourseOrderDetailVo.setPayType( courseOrder.getPayType() );
        adminCourseOrderDetailVo.setThirdTradeNo( courseOrder.getThirdTradeNo() );
        adminCourseOrderDetailVo.setExpireTime( courseOrder.getExpireTime() );
        adminCourseOrderDetailVo.setPayTime( courseOrder.getPayTime() );
        adminCourseOrderDetailVo.setCancelTime( courseOrder.getCancelTime() );
        adminCourseOrderDetailVo.setRefundTime( courseOrder.getRefundTime() );
        adminCourseOrderDetailVo.setCreateTime( courseOrder.getCreateTime() );
        adminCourseOrderDetailVo.setUpdateTime( courseOrder.getUpdateTime() );

        return adminCourseOrderDetailVo;
    }

    @Override
    public CourseOrderQuery reqVoToQuery(AdminCourseOrderPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseOrderQuery courseOrderQuery = new CourseOrderQuery();

        courseOrderQuery.setCurrent( reqVo.getCurrent() );
        courseOrderQuery.setSize( reqVo.getSize() );
        courseOrderQuery.setOrderNo( reqVo.getOrderNo() );
        courseOrderQuery.setUserId( reqVo.getUserId() );
        courseOrderQuery.setCourseId( reqVo.getCourseId() );
        courseOrderQuery.setStatus( reqVo.getStatus() );
        courseOrderQuery.setPayType( reqVo.getPayType() );
        courseOrderQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        courseOrderQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );

        return courseOrderQuery;
    }

    protected AdminCourseOrderBasicVo courseOrderToAdminCourseOrderBasicVo(CourseOrder courseOrder) {
        if ( courseOrder == null ) {
            return null;
        }

        AdminCourseOrderBasicVo adminCourseOrderBasicVo = new AdminCourseOrderBasicVo();

        adminCourseOrderBasicVo.setId( courseOrder.getId() );
        adminCourseOrderBasicVo.setOrderNo( courseOrder.getOrderNo() );
        adminCourseOrderBasicVo.setUserId( courseOrder.getUserId() );
        adminCourseOrderBasicVo.setCourseId( courseOrder.getCourseId() );
        adminCourseOrderBasicVo.setCourseTitle( courseOrder.getCourseTitle() );
        adminCourseOrderBasicVo.setCoverUrl( courseOrder.getCoverUrl() );
        adminCourseOrderBasicVo.setCoursePrice( courseOrder.getCoursePrice() );
        adminCourseOrderBasicVo.setUserCouponId( courseOrder.getUserCouponId() );
        adminCourseOrderBasicVo.setCouponName( courseOrder.getCouponName() );
        adminCourseOrderBasicVo.setDiscountAmount( courseOrder.getDiscountAmount() );
        adminCourseOrderBasicVo.setPayAmount( courseOrder.getPayAmount() );
        adminCourseOrderBasicVo.setStatus( courseOrder.getStatus() );
        adminCourseOrderBasicVo.setPayType( courseOrder.getPayType() );
        adminCourseOrderBasicVo.setExpireTime( courseOrder.getExpireTime() );
        adminCourseOrderBasicVo.setPayTime( courseOrder.getPayTime() );
        adminCourseOrderBasicVo.setCreateTime( courseOrder.getCreateTime() );

        return adminCourseOrderBasicVo;
    }
}
