package org.example;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Use to drop a msg only for one client
 */
public class TrustEvalInterceptor implements PublishOutboundInterceptor, PublishInboundInterceptor {
    private static final @NotNull Logger log = LoggerFactory.getLogger(TrustEvalInterceptor.class);

    @Override
    public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput, @NotNull PublishInboundOutput publishInboundOutput) {
        final String sender = publishInboundInput.getClientInformation().getClientId();
        log.info("Sent by: {}", sender);
    }

    @Override
    public void onOutboundPublish(@NotNull PublishOutboundInput publishOutboundInput, @NotNull PublishOutboundOutput publishOutboundOutput) {
        final String receiver = publishOutboundInput.getClientInformation().getClientId();
        log.info("Sent to: {}", receiver);

    }


}
