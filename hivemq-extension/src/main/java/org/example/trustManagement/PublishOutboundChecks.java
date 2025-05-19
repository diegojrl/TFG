package org.example.trustManagement;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionAttributeStore;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import org.example.trustData.TrustStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class PublishOutboundChecks implements PublishOutboundInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PublishOutboundChecks.class);

    @Override
    public void onOutboundPublish(@NotNull PublishOutboundInput publishOutboundInput, @NotNull PublishOutboundOutput publishOutboundOutput) {
        log.trace("Running outbound checks");
        final String clientId = publishOutboundInput.getClientInformation().getClientId();
        final int qos = publishOutboundInput.getPublishPacket().getQos().getQosNumber();
        final String packetId = String.valueOf(publishOutboundOutput.getPublishPacket().getPacketId());
        log.trace("clientId: {}, packetId: {}, QoS: {}, Topic: {}", clientId, packetId, qos, publishOutboundInput.getPublishPacket().getTopic());

        if (qos > 0) {
            final ConnectionAttributeStore store = publishOutboundInput.getConnectionInformation().getConnectionAttributeStore();
            // Check for packet resends
            if (store.get(packetId).isPresent()) {
                log.debug("Packet resend");
                TrustStore.getTrustAttributes(clientId).addFailedPacket();
            }else {
                log.trace("Packet sent");
                TrustStore.getTrustAttributes(clientId).addSentPacket();
            }
            // Store timestamp for Latency calculation
            log.debug("Save timestamp");
            final long timestamp = System.currentTimeMillis();

            //Store timestamp and packetId in session store
            ByteBuffer timeData = ByteBuffer.allocate(Long.BYTES);
            timeData.putLong(timestamp);
            timeData.position(0);
            store.put(packetId, timeData);
        }
    }
}
