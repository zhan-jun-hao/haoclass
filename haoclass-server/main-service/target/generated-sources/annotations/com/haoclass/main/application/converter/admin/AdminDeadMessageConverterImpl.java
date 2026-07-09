package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.MqDeadMessageQuery;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.main.interfaces.vo.mq.request.AdminMqDeadMessagePageReqVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageBasicRespVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageDetailRespVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminDeadMessageConverterImpl implements AdminDeadMessageConverter {

    @Override
    public MqDeadMessageQuery reqVoToQuery(AdminMqDeadMessagePageReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        MqDeadMessageQuery mqDeadMessageQuery = new MqDeadMessageQuery();

        mqDeadMessageQuery.setCurrent( reqVo.getCurrent() );
        mqDeadMessageQuery.setSize( reqVo.getSize() );
        mqDeadMessageQuery.setCreateTimeStart( reqVo.getCreateTimeStart() );
        mqDeadMessageQuery.setCreateTimeEnd( reqVo.getCreateTimeEnd() );
        mqDeadMessageQuery.setBizType( reqVo.getBizType() );
        mqDeadMessageQuery.setBizId( reqVo.getBizId() );
        mqDeadMessageQuery.setStatus( reqVo.getStatus() );
        mqDeadMessageQuery.setSourceQueue( reqVo.getSourceQueue() );

        return mqDeadMessageQuery;
    }

    @Override
    public List<AdminMqDeadMessageBasicRespVo> poToRespVo(List<MqDeadMessage> mqDeadMessage) {
        if ( mqDeadMessage == null ) {
            return null;
        }

        List<AdminMqDeadMessageBasicRespVo> list = new ArrayList<AdminMqDeadMessageBasicRespVo>( mqDeadMessage.size() );
        for ( MqDeadMessage mqDeadMessage1 : mqDeadMessage ) {
            list.add( mqDeadMessageToAdminMqDeadMessageBasicRespVo( mqDeadMessage1 ) );
        }

        return list;
    }

    @Override
    public AdminMqDeadMessageDetailRespVo poToDetailVo(MqDeadMessage po) {
        if ( po == null ) {
            return null;
        }

        AdminMqDeadMessageDetailRespVo adminMqDeadMessageDetailRespVo = new AdminMqDeadMessageDetailRespVo();

        adminMqDeadMessageDetailRespVo.setId( po.getId() );
        adminMqDeadMessageDetailRespVo.setBizType( po.getBizType() );
        adminMqDeadMessageDetailRespVo.setBizId( po.getBizId() );
        adminMqDeadMessageDetailRespVo.setSourceQueue( po.getSourceQueue() );
        adminMqDeadMessageDetailRespVo.setSourceExchange( po.getSourceExchange() );
        adminMqDeadMessageDetailRespVo.setSourceRoutingKey( po.getSourceRoutingKey() );
        adminMqDeadMessageDetailRespVo.setDeadQueue( po.getDeadQueue() );
        adminMqDeadMessageDetailRespVo.setDeadExchange( po.getDeadExchange() );
        adminMqDeadMessageDetailRespVo.setDeadRoutingKey( po.getDeadRoutingKey() );
        adminMqDeadMessageDetailRespVo.setMessageBody( po.getMessageBody() );
        adminMqDeadMessageDetailRespVo.setHeaders( po.getHeaders() );
        adminMqDeadMessageDetailRespVo.setDeadReason( po.getDeadReason() );
        adminMqDeadMessageDetailRespVo.setLastError( po.getLastError() );
        adminMqDeadMessageDetailRespVo.setStatus( po.getStatus() );
        adminMqDeadMessageDetailRespVo.setRetryCount( po.getRetryCount() );
        adminMqDeadMessageDetailRespVo.setLastRetryTime( po.getLastRetryTime() );
        adminMqDeadMessageDetailRespVo.setCreateTime( po.getCreateTime() );
        adminMqDeadMessageDetailRespVo.setUpdateTime( po.getUpdateTime() );

        return adminMqDeadMessageDetailRespVo;
    }

    protected AdminMqDeadMessageBasicRespVo mqDeadMessageToAdminMqDeadMessageBasicRespVo(MqDeadMessage mqDeadMessage) {
        if ( mqDeadMessage == null ) {
            return null;
        }

        AdminMqDeadMessageBasicRespVo adminMqDeadMessageBasicRespVo = new AdminMqDeadMessageBasicRespVo();

        adminMqDeadMessageBasicRespVo.setId( mqDeadMessage.getId() );
        adminMqDeadMessageBasicRespVo.setBizType( mqDeadMessage.getBizType() );
        adminMqDeadMessageBasicRespVo.setBizId( mqDeadMessage.getBizId() );
        adminMqDeadMessageBasicRespVo.setSourceQueue( mqDeadMessage.getSourceQueue() );
        adminMqDeadMessageBasicRespVo.setDeadReason( mqDeadMessage.getDeadReason() );
        adminMqDeadMessageBasicRespVo.setLastError( mqDeadMessage.getLastError() );
        adminMqDeadMessageBasicRespVo.setStatus( mqDeadMessage.getStatus() );
        adminMqDeadMessageBasicRespVo.setRetryCount( mqDeadMessage.getRetryCount() );
        adminMqDeadMessageBasicRespVo.setCreateTime( mqDeadMessage.getCreateTime() );

        return adminMqDeadMessageBasicRespVo;
    }
}
