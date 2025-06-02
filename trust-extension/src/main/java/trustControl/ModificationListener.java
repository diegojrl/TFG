package trustControl;

import authorization.PolicyDecisionPoint;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.AckReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustData.DeviceTrustAttributes;
import trustData.TrustStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class ModificationListener implements PublishInboundInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ModificationListener.class);
    private static final String MOD_TOPIC = "control/mod/";

    private final PolicyDecisionPoint pdp;

    public ModificationListener() {
        try {
            pdp = PolicyDecisionPoint.getInstance();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInboundPublish(@NotNull PublishInboundInput publishInboundInput, @NotNull PublishInboundOutput publishInboundOutput) {
        final PublishPacket pub = publishInboundInput.getPublishPacket();
        final ClientInformation clientInfo = publishInboundInput.getClientInformation();
        final ConnectionInformation connInfo = publishInboundInput.getConnectionInformation();

        if (!pdp.authorizePublish(clientInfo, connInfo, pub))
            return;

        final String topic = pub.getTopic();
        if (topic.startsWith(MOD_TOPIC)) {

            final int clientIdIdx = topic.indexOf('/', MOD_TOPIC.length());

            if (clientIdIdx != -1) {

                final String clientId = topic.substring(MOD_TOPIC.length(), clientIdIdx);
                final String attr = topic.substring(clientIdIdx + 1);
                final DeviceTrustAttributes trustAttributes = TrustStore.get(clientId);

                if (trustAttributes != null) {

                    Optional<ByteBuffer> payload = publishInboundInput.getPublishPacket().getPayload();
                    switch (attr) {
                        case "ping":
                            if (payload.isPresent() && payload.get().remaining() == 4) {
                                int latency = payload.get().getInt();
                                log.debug("Set {} latency to {}", clientId, latency);
                                trustAttributes.setLatency(latency);
                            } else {
                                log.info("Wrong ping modification message for device {}:", clientId);
                            }
                            break;
                        case "failPctr":
                            if (payload.isPresent() && payload.get().remaining() == 4) {
                                int failureRate = payload.get().getInt();
                                log.debug("Set {} failure rate to {}", clientId, failureRate);
                                trustAttributes.setFailureRate(failureRate);
                            } else {
                                log.info("Wrong failureRate modification message for device {}:", clientId);
                            }
                            break;
                        case "rep":
                            break;
                        default:
                            log.error("Unknown attribute: {}", attr);
                    }

                } else {
                    log.info("No device with clientId: {}", clientId);
                }
            } else {
                log.error("Modification with invalid clientId: {}", topic);
            }

            publishInboundOutput.preventPublishDelivery(AckReasonCode.SUCCESS);
        }
    }
}
