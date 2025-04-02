package org.example;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import com.hivemq.extension.sdk.api.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ControlSub  {
    private static final String CONTROL_TOPIC = "control";
    private static final Logger log = LoggerFactory.getLogger(ControlSub.class);

    public static final Runnable controlTask = () -> {
        log.debug("Trust control update");
        Services.clientService().iterateAllClients((itContext, session) -> {
            if (session.isConnected()) {
                log.info("{}", TrustStore.getTrustAttributes(session.getClientIdentifier()).getLatency());

            }
        }).join();
    };
}
