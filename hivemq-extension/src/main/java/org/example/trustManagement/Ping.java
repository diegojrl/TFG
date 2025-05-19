package org.example.trustManagement;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionAttributeStore;
import com.hivemq.extension.sdk.api.interceptor.pingreq.PingReqInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.puback.PubackInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackInboundInput;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecInboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import org.example.trustData.DeviceTrustAttributes;
import org.example.trustData.TrustStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Optional;

public class Ping implements PingReqInboundInterceptor, PubackInboundInterceptor, PubrecInboundInterceptor {
    private static final Logger log = LoggerFactory.getLogger(Ping.class);

    private static final String PING_TOPIC = "tmgr/ping";

    /**
     *
     */
    @Override
    public void onInboundPingReq(@NotNull PingReqInboundInput pingReqInboundInput, @NotNull PingReqInboundOutput pingReqInboundOutput) {
        log.debug("Received PingReq");


        final String clientId = pingReqInboundInput.getClientInformation().getClientId();


        final Publish pingMsg = Builders.publish()
                .topic(PING_TOPIC)
                .payload(ByteBuffer.allocate(0))
                .qos(Qos.EXACTLY_ONCE)
                .build();

        Services.publishService().publishToClient(pingMsg, clientId).join();
    }


    @Override
    public void onInboundPubrec(@NotNull PubrecInboundInput pubrecInboundInput, @NotNull PubrecInboundOutput pubrecInboundOutput) {
        log.debug("Received Pubrec {}", pubrecInboundInput.getPubrecPacket().getPacketIdentifier());
        final long currentTime = System.currentTimeMillis();
        final String clientId = pubrecInboundInput.getClientInformation().getClientId();
        final Integer packetId = pubrecInboundInput.getPubrecPacket().getPacketIdentifier();
        final ConnectionAttributeStore store = pubrecInboundInput.getConnectionInformation().getConnectionAttributeStore();

        updateLatency(store, clientId, packetId, currentTime);
    }

    /**
     *
     */
    @Override
    public void onInboundPuback(@NotNull PubackInboundInput pubackInboundInput, @NotNull PubackInboundOutput pubackInboundOutput) {
        log.debug("Received Puback {}", pubackInboundInput.getPubackPacket().getPacketIdentifier());
        final long currentTime = System.currentTimeMillis();
        final String clientId = pubackInboundInput.getClientInformation().getClientId();
        final Integer packetId = pubackInboundInput.getPubackPacket().getPacketIdentifier();
        final ConnectionAttributeStore store = pubackInboundInput.getConnectionInformation().getConnectionAttributeStore();

        updateLatency(store, clientId, packetId, currentTime);

    }

    private static void updateLatency(@NotNull final ConnectionAttributeStore store, @NotNull final String clientId, @NotNull final Integer packetId, @NotNull final long currentTime) {
        log.debug("Updating latency for clientId: {} packetId: {}", clientId, packetId);
        Optional<ByteBuffer> timeData = store.remove(packetId.toString());
        if (timeData.isPresent()) {
            final long timestamp = timeData.get().getLong();
            //Round trip time
            final long rtt = (currentTime - timestamp);
            final long latency = rtt / 2;

            final DeviceTrustAttributes trustAttributes = TrustStore.getTrustAttributes(clientId);
            if (trustAttributes != null){
                trustAttributes.addLatency(latency);
                log.debug("{} latency: {}", clientId, latency);
            } else {
                log.error("No client with id {} found", clientId);
            }

        } else {
            log.debug("No time data for packet {}, client {}", packetId, clientId);
        }
    }

}
