package trustManagement;

import authorization.PolicyDecisionPoint;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.AckReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustData.DeviceTrustAttributes;
import trustData.TrustStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class ReputationListener implements PublishInboundInterceptor, SubscribeInboundInterceptor {
    public static final String REPUTATION_TOPIC = "tmgr/rep/";
    public static final ManagedExtensionExecutorService executor = Services.extensionExecutorService();
    private static final Logger log = LoggerFactory.getLogger(ReputationListener.class);
    private final PolicyDecisionPoint pdp;

    public ReputationListener() {
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
        if (topic.startsWith(REPUTATION_TOPIC)) {
            final var output = publishInboundOutput.async(Duration.ofMillis(150));
            executor.submit(() -> {
                final String clientId = publishInboundInput.getClientInformation().getClientId();
                final String target = topic.substring(REPUTATION_TOPIC.length());
                if (!clientId.equals(target)) {
                    final Optional<ByteBuffer> payload = pub.getPayload();
                    if (payload.isPresent() && payload.get().remaining() == Float.BYTES) {
                        final float opinion = payload.get().getFloat();
                        final DeviceTrustAttributes dev = TrustStore.get(target);
                        if (dev != null) {
                            log.debug("Adding opinion {} to {}, from {}", opinion, target, clientId);
                            dev.addOpinion(clientId, opinion);
                            output.getOutput().preventPublishDelivery(AckReasonCode.SUCCESS);
                        } else {
                            log.error("{} is not connected, cannot modify opinion", target);
                            output.getOutput().preventPublishDelivery(AckReasonCode.UNSPECIFIED_ERROR, "Target client not connected");
                        }
                    } else {
                        log.error("Missing or invalid payload in reputation msg");
                        output.getOutput().preventPublishDelivery(AckReasonCode.PAYLOAD_FORMAT_INVALID, "Invalid payload");
                    }
                } else {
                    log.error("{} tried to modify its own reputation", clientId);
                    output.getOutput().preventPublishDelivery(AckReasonCode.TOPIC_NAME_INVALID, "Client cannot modify its own reputation");
                }
                output.resume();
            });
        }
    }

    @Override
    public void onInboundSubscribe(@NotNull SubscribeInboundInput subscribeInboundInput, @NotNull SubscribeInboundOutput subscribeInboundOutput) {
        final ClientInformation clientInfo = subscribeInboundInput.getClientInformation();
        final ConnectionInformation connInfo = subscribeInboundInput.getConnectionInformation();
        final List<Subscription> subs = subscribeInboundInput.getSubscribePacket().getSubscriptions();
        for (Subscription sub : subs) {
            if (sub.getTopicFilter().startsWith(REPUTATION_TOPIC)) {
                if (pdp.authorizeSubscription(clientInfo, connInfo, sub)) {
                    final String target = sub.getTopicFilter().substring(REPUTATION_TOPIC.length());
                    if (target.equals("+")) {
                        //Todo
                    } else {
                        //Todo
                    }
                }
            }
        }
    }
}
