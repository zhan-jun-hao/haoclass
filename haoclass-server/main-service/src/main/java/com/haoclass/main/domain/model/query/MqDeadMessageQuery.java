package com.haoclass.main.domain.model.query;

import com.haoclass.common.query.TimeRangePageQuery;
import com.haoclass.main.infrastructure.common.enums.MqDeadMessageStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MqDeadMessageQuery extends TimeRangePageQuery {

    private String bizType;

    private String bizId;

    private MqDeadMessageStatusEnum status;

    private String sourceQueue;

}
