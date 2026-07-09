package com.haoclass.main.application.converter.client;

import com.haoclass.main.domain.model.query.CourseOrderQuery;
import com.haoclass.main.infrastructure.persistence.po.CourseOrder;
import com.haoclass.main.interfaces.vo.order.client.request.ClientCourseOrderPageQueryReqVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderBasicVo;
import com.haoclass.main.interfaces.vo.order.client.response.CourseOrderDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class ClientCourseOrderConverterImpl implements ClientCourseOrderConverter {

    @Override
    public List<CourseOrderBasicVo> poToBasicVo(List<CourseOrder> courseOrderList) {
        if ( courseOrderList == null ) {
            return null;
        }

        List<CourseOrderBasicVo> list = new ArrayList<CourseOrderBasicVo>( courseOrderList.size() );
        for ( CourseOrder courseOrder : courseOrderList ) {
            list.add( poToBasicVo( courseOrder ) );
        }

        return list;
    }

    @Override
    public CourseOrderBasicVo poToBasicVo(CourseOrder courseOrder) {
        if ( courseOrder == null ) {
            return null;
        }

        CourseOrderBasicVo courseOrderBasicVo = new CourseOrderBasicVo();

        courseOrderBasicVo.setId( courseOrder.getId() );
        courseOrderBasicVo.setOrderNo( courseOrder.getOrderNo() );
        courseOrderBasicVo.setUserId( courseOrder.getUserId() );
        courseOrderBasicVo.setCourseId( courseOrder.getCourseId() );
        courseOrderBasicVo.setCourseTitle( courseOrder.getCourseTitle() );
        courseOrderBasicVo.setCoverUrl( courseOrder.getCoverUrl() );
        courseOrderBasicVo.setCoursePrice( courseOrder.getCoursePrice() );
        courseOrderBasicVo.setUserCouponId( courseOrder.getUserCouponId() );
        courseOrderBasicVo.setCouponName( courseOrder.getCouponName() );
        courseOrderBasicVo.setDiscountAmount( courseOrder.getDiscountAmount() );
        courseOrderBasicVo.setPayAmount( courseOrder.getPayAmount() );
        courseOrderBasicVo.setStatus( courseOrder.getStatus() );
        courseOrderBasicVo.setPayType( courseOrder.getPayType() );
        courseOrderBasicVo.setExpireTime( courseOrder.getExpireTime() );
        courseOrderBasicVo.setPayTime( courseOrder.getPayTime() );
        courseOrderBasicVo.setCreateTime( courseOrder.getCreateTime() );

        return courseOrderBasicVo;
    }

    @Override
    public CourseOrderDetailVo poToDetailVo(CourseOrder courseOrder) {
        if ( courseOrder == null ) {
            return null;
        }

        CourseOrderDetailVo courseOrderDetailVo = new CourseOrderDetailVo();

        courseOrderDetailVo.setId( courseOrder.getId() );
        courseOrderDetailVo.setOrderNo( courseOrder.getOrderNo() );
        courseOrderDetailVo.setUserId( courseOrder.getUserId() );
        courseOrderDetailVo.setCourseId( courseOrder.getCourseId() );
        courseOrderDetailVo.setCourseTitle( courseOrder.getCourseTitle() );
        courseOrderDetailVo.setCoverUrl( courseOrder.getCoverUrl() );
        courseOrderDetailVo.setCoursePrice( courseOrder.getCoursePrice() );
        courseOrderDetailVo.setUserCouponId( courseOrder.getUserCouponId() );
        courseOrderDetailVo.setCouponName( courseOrder.getCouponName() );
        courseOrderDetailVo.setDiscountAmount( courseOrder.getDiscountAmount() );
        courseOrderDetailVo.setPayAmount( courseOrder.getPayAmount() );
        courseOrderDetailVo.setStatus( courseOrder.getStatus() );
        courseOrderDetailVo.setPayType( courseOrder.getPayType() );
        courseOrderDetailVo.setThirdTradeNo( courseOrder.getThirdTradeNo() );
        courseOrderDetailVo.setExpireTime( courseOrder.getExpireTime() );
        courseOrderDetailVo.setPayTime( courseOrder.getPayTime() );
        courseOrderDetailVo.setCancelTime( courseOrder.getCancelTime() );
        courseOrderDetailVo.setRefundTime( courseOrder.getRefundTime() );
        courseOrderDetailVo.setCreateTime( courseOrder.getCreateTime() );
        courseOrderDetailVo.setUpdateTime( courseOrder.getUpdateTime() );

        return courseOrderDetailVo;
    }

    @Override
    public CourseOrderQuery reqVoToQuery(ClientCourseOrderPageQueryReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        CourseOrderQuery courseOrderQuery = new CourseOrderQuery();

        courseOrderQuery.setCurrent( reqVo.getCurrent() );
        courseOrderQuery.setSize( reqVo.getSize() );
        courseOrderQuery.setStatus( reqVo.getStatus() );

        return courseOrderQuery;
    }
}
