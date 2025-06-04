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
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustData.DeviceTrustAttributes;
import trustData.TrustStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;

public class ModificationListener implements PublishInboundInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ModificationListener.class);
    private static final ManagedExtensionExecutorService executor = Services.extensionExecutorService();
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

        final String topic = pub.getTopic();
        if (topic.startsWith(MOD_TOPIC)) {
            var output = publishInboundOutput.async(Duration.ofMillis(100));
            executor.submit(() -> {
                if (!pdp.authorizePublish(clientInfo, connInfo, pub)) {
                    output.getOutput().preventPublishDelivery(AckReasonCode.NOT_AUTHORIZED);
                    output.resume();
                    log.error("modification authz error, client: {}, topic: {}", clientInfo.getClientId(), topic);
                    return;
                }


                final int clientIdIdx = topic.indexOf('/', MOD_TOPIC.length());
                log.debug("mod idx: {}", clientIdIdx);
                if (clientIdIdx != -1) {

                    final String clientId = topic.substring(MOD_TOPIC.length(), clientIdIdx);
                    final String attr = topic.substring(clientIdIdx + 1);
                    final DeviceTrustAttributes trustAttributes = TrustStore.get(clientId);
                    log.debug("mod action: {}", attr);
                    if (trustAttributes != null) {

                        log.debug("mod dev: {}", trustAttributes.getClientId());
                        Optional<ByteBuffer> payload = publishInboundInput.getPublishPacket().getPayload();
                        switch (attr) {
                            case "ping":
                                if (payload.isPresent() && payload.get().remaining() == 4) {
                                    int latency = payload.get().getInt();
                                    log.debug("Set {} latency to {}", clientId, latency);
                                    trustAttributes.setLatency(latency);
                                    output.getOutput().preventPublishDelivery(AckReasonCode.SUCCESS);
                                } else {
                                    log.info("Wrong ping modification message for device {}:", clientId);
                                    output.getOutput().preventPublishDelivery(AckReasonCode.IMPLEMENTATION_SPECIFIC_ERROR, "Invalid ping data");
                                }
                                break;
                            case "failPctr":
                                if (payload.isPresent() && payload.get().remaining() == 4) {
                                    int failureRate = payload.get().getInt();
                                    log.debug("Set {} failure rate to {}", clientId, failureRate);
                                    trustAttributes.setFailureRate(failureRate);
                                    output.getOutput().preventPublishDelivery(AckReasonCode.SUCCESS);
                                } else {
                                    log.info("Wrong failureRate modification message for device {}:", clientId);
                                    output.getOutput().preventPublishDelivery(AckReasonCode.IMPLEMENTATION_SPECIFIC_ERROR, "Invalid failPctr data");
                                }
                                break;
                            case "rep":
                                try {
                                    Database.deleteAllOpinions(clientId);
                                    trustAttributes.updateReputation();
                                    output.getOutput().preventPublishDelivery(AckReasonCode.SUCCESS);
                                } catch (SQLException e) {
                                    log.error("Error deleting opinions for clientId {}, {}", clientId, e.getMessage());
                                    output.getOutput().preventPublishDelivery(AckReasonCode.UNSPECIFIED_ERROR);
                                }
                                break;
                            default:
                                log.error("Unknown attribute: {}", attr);
                                output.getOutput().preventPublishDelivery(AckReasonCode.IMPLEMENTATION_SPECIFIC_ERROR, "Unknown attribute: " + attr);
                        }

                    } else {
                        log.info("No device with clientId: {}", clientId);
                        output.getOutput().preventPublishDelivery(AckReasonCode.IMPLEMENTATION_SPECIFIC_ERROR, "Unknown client: " + clientId);
                    }
                } else {
                    log.error("Modification with invalid clientId: {}", topic);
                    output.getOutput().preventPublishDelivery(AckReasonCode.IMPLEMENTATION_SPECIFIC_ERROR, "Invalid clientId: " + topic);
                }
                output.resume();
            });
        }
    }
}
