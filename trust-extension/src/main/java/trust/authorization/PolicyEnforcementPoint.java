package trust.authorization;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.async.TimeoutFallback;
import com.hivemq.extension.sdk.api.auth.PublishAuthorizer;
import com.hivemq.extension.sdk.api.auth.SubscriptionAuthorizer;
import com.hivemq.extension.sdk.api.auth.parameter.PublishAuthorizerInput;
import com.hivemq.extension.sdk.api.auth.parameter.PublishAuthorizerOutput;
import com.hivemq.extension.sdk.api.auth.parameter.SubscriptionAuthorizerInput;
import com.hivemq.extension.sdk.api.auth.parameter.SubscriptionAuthorizerOutput;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.packets.publish.AckReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubackReasonCode;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class PolicyEnforcementPoint implements SubscriptionAuthorizer, PublishAuthorizer {
    private static final ManagedExtensionExecutorService executor = Services.extensionExecutorService();
    private static final Logger log = LoggerFactory.getLogger(PolicyEnforcementPoint.class);
    private final PolicyDecisionPoint pdp;

    public PolicyEnforcementPoint() throws IOException {
        pdp = PolicyDecisionPoint.getInstance();
    }

    @Override
    public void authorizeSubscribe(@NotNull SubscriptionAuthorizerInput subscriptionAuthorizerInput, @NotNull SubscriptionAuthorizerOutput subscriptionAuthorizerOutput) {
        final ClientInformation clientInfo = subscriptionAuthorizerInput.getClientInformation();
        final ConnectionInformation conInfo = subscriptionAuthorizerInput.getConnectionInformation();
        final Subscription sub = subscriptionAuthorizerInput.getSubscription();

        final var output = subscriptionAuthorizerOutput.async(Duration.ofMillis(100), TimeoutFallback.FAILURE);
        executor.submit(() -> {
            if (pdp.authorizeSubscription(clientInfo, conInfo, sub)) {
                log.debug("Successfully authorized subscription, t: {}, client: {}", sub.getTopicFilter(), clientInfo.getClientId());
                output.getOutput().authorizeSuccessfully();
            } else {
                output.getOutput().failAuthorization(SubackReasonCode.NOT_AUTHORIZED);
                log.debug("Rejected subscription, t: {}, client: {}", sub.getTopicFilter(), clientInfo.getClientId());
            }
            output.resume();
        });
    }


    @Override
    public void authorizePublish(@NotNull PublishAuthorizerInput publishAuthorizerInput, @NotNull PublishAuthorizerOutput publishAuthorizerOutput) {
        final ClientInformation clientInfo = publishAuthorizerInput.getClientInformation();
        final ConnectionInformation conInfo = publishAuthorizerInput.getConnectionInformation();
        final PublishPacket pub = publishAuthorizerInput.getPublishPacket();


        final var output = publishAuthorizerOutput.async(Duration.ofMillis(100), TimeoutFallback.FAILURE);
        executor.submit(() -> {
            if (pdp.authorizePublish(clientInfo, conInfo, pub)) {
                log.debug("Successful publish, t: {}, client: {}", pub.getTopic(), clientInfo.getClientId());
                output.getOutput().authorizeSuccessfully();
            } else {
                output.getOutput().failAuthorization(AckReasonCode.NOT_AUTHORIZED);
                log.debug("Rejected  publish, t: {}, client: {}", pub.getTopic(), clientInfo.getClientId());
            }
            output.resume();
        });
    }
}
