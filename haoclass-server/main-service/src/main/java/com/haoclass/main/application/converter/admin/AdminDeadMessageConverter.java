package com.haoclass.main.application.converter.admin;

import com.haoclass.main.domain.model.query.MqDeadMessageQuery;
import com.haoclass.main.infrastructure.persistence.po.MqDeadMessage;
import com.haoclass.main.interfaces.vo.mq.request.AdminMqDeadMessagePageReqVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageBasicRespVo;
import com.haoclass.main.interfaces.vo.mq.response.AdminMqDeadMessageDetailRespVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminDeadMessageConverter {

    AdminDeadMessageConverter INSTANCE = Mappers.getMapper(AdminDeadMessageConverter.class);

    MqDeadMessageQuery reqVoToQuery(AdminMqDeadMessagePageReqVo reqVo);

    List<AdminMqDeadMessageBasicRespVo> poToRespVo(List<MqDeadMessage> mqDeadMessage);

    AdminMqDeadMessageDetailRespVo poToDetailVo(MqDeadMessage po);
}
