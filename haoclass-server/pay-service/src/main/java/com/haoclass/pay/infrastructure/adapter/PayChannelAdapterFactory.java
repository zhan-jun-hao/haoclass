package com.haoclass.pay.infrastructure.adapter;

import com.haoclass.common.exception.BusinessException;
import com.haoclass.pay.infrastructure.common.enums.PaymentChannelEnum;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PayChannelAdapterFactory {

    private final Map<PaymentChannelEnum, PayChannelAdapter> adapterMap;

    public PayChannelAdapterFactory(List<PayChannelAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(PayChannelAdapter::getChannel, Function.identity()));
    }

    public PayChannelAdapter getAdapter(PaymentChannelEnum channel) {
        PayChannelAdapter adapter = adapterMap.get(channel);
        if (adapter == null) {
            throw BusinessException.badRequest("暂不支持该支付渠道");
        }
        return adapter;
    }
}
