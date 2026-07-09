package com.haoclass.pay.api.common.contants;

import com.haoclass.common.trace.TraceConstants;

/**
 * 消息header字段
 */
public interface MqMessageHeadersKeyConstants {

    String TARGET_EXCHANGE = "targetExchange";

    String TARGET_QUEUE = "targetQueue";

    String TARGET_ROUTING_KEY = "targetRoutingKey";

    String MESSAGE_ID = "mqMessageId";

    String TRACE_ID = TraceConstants.TRACE_ID_HEADER;

}
