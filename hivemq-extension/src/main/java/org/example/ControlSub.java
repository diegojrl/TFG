package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


public class ControlSub  {
    private static final String CONTROL_TOPIC = "control";
    private static final String VIEW_TOPIC = CONTROL_TOPIC + "/view/";
    private static final Logger log = LoggerFactory.getLogger(ControlSub.class);

    public static final Runnable controlTask = () -> {
        log.debug("Trust control update");
        Services.clientService().iterateAllClients((itContext, session) -> {
            if (session.isConnected()) {
                final String clientId = session.getClientIdentifier();
                final TrustAttributes trustAttributes = TrustStore.getTrustAttributes(clientId);
                if (trustAttributes != null) {
                    try {
                        //Encode data to byteBuffer
                        final ByteBuffer payload = trustAttributes.encode();

                        //Build message
                        Publish msg = Builders.publish()
                                .topic(VIEW_TOPIC+clientId)     //Topic -> "control/view/<clientId>"
                                .payload(payload)
                                .build();

                        //Send the message
                        Services.publishService().publish(msg).join();
                    } catch (JsonProcessingException e) {
                        log.error("Error encoding trust attributes for client {}",clientId);
                    }
                }
            }
        }).join();
    };
}
