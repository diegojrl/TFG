package trust.trustControl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trust.trustData.DeviceTrustAttributes;
import trust.trustData.TrustStore;

import java.nio.ByteBuffer;
import java.util.concurrent.*;


public class ControlSub {
    public static final int RUN_INTERVAL_SEC = 15;

    public static final String VIEW_TOPIC = "control/view/";
    private static final Logger log = LoggerFactory.getLogger(ControlSub.class);
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static final Runnable controlTask = () -> {
        log.debug("Trust control update");
        Services.clientService().iterateAllClients((itContext, session) -> {
            if (session.isConnected()) {
                final String clientId = session.getClientIdentifier();
                final DeviceTrustAttributes trustAttributes = TrustStore.get(clientId);
                if (trustAttributes != null) {
                    try {
                        publishDevice(trustAttributes).get(5, TimeUnit.SECONDS);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        log.error("Cant send control message, took too long");
                    }
                }
            }
        }, executor).join();
        log.debug("Trust control update done");
    };

    public static CompletableFuture<Void> publishDevice(DeviceTrustAttributes device) {
        try {
            //Encode data to byteBuffer
            final ByteBuffer payload = device.encodeToControlFormat();

            //Build message
            Publish msg = Builders.publish()
                    .topic(VIEW_TOPIC + device.getClientId())     //Topic -> "control/view/<clientId>"
                    .payload(payload)
                    .retain(true)
                    .messageExpiryInterval(RUN_INTERVAL_SEC - 1)
                    .qos(Qos.EXACTLY_ONCE)
                    .build();
            log.trace("Publishing message: {}", msg);
            //Send the message
            return Services.publishService().publish(msg);
        } catch (JsonProcessingException e) {
            log.error("Error encoding trust attributes for client {}", device.getClientId());
        }
        return CompletableFuture.completedFuture(null);
    }
}
