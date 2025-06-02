package trustManagement;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.async.TimeoutFallback;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionAttributeStore;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundOutput;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustData.TrustStore;

import java.nio.ByteBuffer;
import java.time.Duration;

public class PublishOutboundChecks implements PublishOutboundInterceptor, PubrecOutboundInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PublishOutboundChecks.class);
    private static final ManagedExtensionExecutorService executor = Services.extensionExecutorService();

    @Override
    public void onOutboundPublish(@NotNull PublishOutboundInput publishOutboundInput, @NotNull PublishOutboundOutput publishOutboundOutput) {
        log.trace("Running outbound checks");
        final int qos = publishOutboundInput.getPublishPacket().getQos().getQosNumber();
        final String clientId = publishOutboundInput.getClientInformation().getClientId();
        final String packetId = String.valueOf(publishOutboundOutput.getPublishPacket().getPacketId());
        log.trace("clientId: {}, packetId: {}, QoS: {}, Topic: {}", clientId, packetId, qos, publishOutboundInput.getPublishPacket().getTopic());

        if (qos > 0) {
            final var output = publishOutboundOutput.async(Duration.ofMillis(50), TimeoutFallback.FAILURE);
            executor.submit(() -> {
                final ConnectionAttributeStore store = publishOutboundInput.getConnectionInformation().getConnectionAttributeStore();
                timestamp_checks(packetId, clientId, store);
                output.resume();
            });
        }
    }

    @Override
    public void onOutboundPubrec(@NotNull PubrecOutboundInput pubrecOutboundInput, @NotNull PubrecOutboundOutput pubrecOutboundOutput) {
        log.trace("Recived pubrec packet");
        final var output = pubrecOutboundOutput.async(Duration.ofMillis(50));
        executor.submit(() -> {
            final String clientId = pubrecOutboundInput.getClientInformation().getClientId();
            final String packetId = String.valueOf(pubrecOutboundOutput.getPubrecPacket().getPacketIdentifier());

            final ConnectionAttributeStore store = pubrecOutboundInput.getConnectionInformation().getConnectionAttributeStore();
            timestamp_checks(packetId, clientId, store);
            output.resume();
        });

    }

    private static void timestamp_checks(final String packetId, final String clientId, final ConnectionAttributeStore store) {
        // Check for packet resends
        if (store.get(packetId).isPresent()) {
            log.debug("Packet resend");
            TrustStore.get(clientId).addFailedPacket();
        } else {
            log.trace("Packet sent");
            TrustStore.get(clientId).addSentPacket();
        }
        // Store timestamp for Latency calculation
        final long timestamp = System.currentTimeMillis();

        //Store timestamp and packetId in session store
        ByteBuffer timeData = ByteBuffer.allocate(Long.BYTES);
        timeData.putLong(timestamp);
        timeData.position(0);
        store.put(packetId, timeData);

    }
}
