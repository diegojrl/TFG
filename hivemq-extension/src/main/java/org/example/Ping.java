package org.example;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.pingreq.PingReqInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.PingRespOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Ping implements PingReqInboundInterceptor, PingRespOutboundInterceptor {

    private static final Logger log = LoggerFactory.getLogger(Ping.class);
    private final Map<String,Long> pingTimes = new ConcurrentHashMap<>();
    @Override
    public void onInboundPingReq(@NotNull PingReqInboundInput pingReqInboundInput, @NotNull PingReqInboundOutput pingReqInboundOutput) {
        final long currentTime = System.currentTimeMillis();
        final String clientId = pingReqInboundInput.getClientInformation().getClientId();
        pingTimes.put(clientId, currentTime);
    }

    @Override
    public void onOutboundPingResp(@NotNull PingRespOutboundInput pingRespOutboundInput, @NotNull PingRespOutboundOutput pingRespOutboundOutput) {
        final long currentTime = System.currentTimeMillis();
        final String clientId = pingRespOutboundInput.getClientInformation().getClientId();
        Long recvTime = pingTimes.remove(clientId);
        if (recvTime == null) recvTime = 0L;
        final long latency = currentTime - recvTime;
        TrustAttributes trustAttributes = TrustStore.getTrustAttributes(clientId);
        trustAttributes.addLatency(latency);
        log.info("{}: latency: {} ms", clientId, trustAttributes.getLatency());
    }
}
