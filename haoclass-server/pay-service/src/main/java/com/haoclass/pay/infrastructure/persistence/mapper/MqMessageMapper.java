package com.haoclass.pay.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoclass.pay.infrastructure.persistence.po.MqMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {
}
