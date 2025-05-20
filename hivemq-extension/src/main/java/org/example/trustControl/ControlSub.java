package org.example.trustControl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import org.example.trustData.DeviceTrustAttributes;
import org.example.trustData.TrustStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.*;


public class ControlSub  {
    private static final String CONTROL_TOPIC = "control";
    private static final String VIEW_TOPIC = CONTROL_TOPIC + "/view/";
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
                        //Encode data to byteBuffer
                        final ByteBuffer payload = trustAttributes.encodeToControlFormat();

                        //Build message
                        Publish msg = Builders.publish()
                                .topic(VIEW_TOPIC+clientId)     //Topic -> "control/view/<clientId>"
                                .payload(payload)
                                .qos(Qos.valueOf(2))
                                .build();
                        log.trace("Publishing message: {}", msg);
                        //Send the message
                        try {
                            Services.publishService().publish(msg).get(5, TimeUnit.SECONDS);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            log.error("Cant send control message, took too long");
                        }

                    } catch (JsonProcessingException e) {
                        log.error("Error encoding trust attributes for client {}",clientId);
                    }
                }
            }
        }, executor).join();
        log.debug("Trust control update done");
    };
}
