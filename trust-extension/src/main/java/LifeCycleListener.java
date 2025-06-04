/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.subscription.TopicSubscription;
import db.Database;
import db.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustControl.ControlSub;
import trustData.DeviceTrustAttributes;
import trustData.TrustStore;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Optional;

import static trustControl.ControlSub.VIEW_TOPIC;
import static trustManagement.PingInterceptor.PING_TOPIC;
import static trustManagement.ReputationListener.REPUTATION_TOPIC;

/**
 * This is a very simple {@link ClientLifecycleEventListener}
 * which logs the MQTT version and identifier of every connecting client.
 *
 * @author Florian Limpöck
 * @since 4.0.0
 */
public class LifeCycleListener implements ClientLifecycleEventListener {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleListener.class);

    @Override
    public void onMqttConnectionStart(final @NotNull ConnectionStartInput connectionStartInput) {
        TopicSubscription pingSub = Builders.topicSubscription()
                .topicFilter(PING_TOPIC)
                .qos(Qos.AT_LEAST_ONCE)
                .build();
        Services.subscriptionStore().addSubscription(PING_TOPIC, pingSub);
    }

    @Override
    public void onAuthenticationSuccessful(final @NotNull AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        final String clientId = authenticationSuccessfulInput.getClientInformation().getClientId();
        final Optional<String> username = authenticationSuccessfulInput.getConnectionInformation().getConnectionAttributeStore().getAsString("username");
        if (username.isPresent()) {
            try {
                // Get data from database
                int userId = Database.insertUser(username.get());
                Device dev = Database.getDevice(clientId);
                DeviceTrustAttributes deviceTrust;

                if (dev != null) {
                    if (dev.userId != userId) {
                        Database.changeDeviceOwner(clientId, dev.userId);
                    }
                    deviceTrust = dev.toTrustAttributes(authenticationSuccessfulInput.getConnectionInformation());
                } else {
                    deviceTrust = new DeviceTrustAttributes(authenticationSuccessfulInput.getClientInformation(), authenticationSuccessfulInput.getConnectionInformation());
                    Database.insertDevice(new Device(deviceTrust, userId));
                }

                TrustStore.put(clientId, deviceTrust);
                log.info("Authentication successful, added to truststore");

                //Broadcast connected client
                final Publish pub = Builders.publish()
                        .topic(REPUTATION_TOPIC + clientId)
                        .payload(ByteBuffer.allocate(0))
                        .build();
                Services.publishService().publish(pub).join();
                ControlSub.publishDevice(deviceTrust);
                log.debug("Broadcast {} connected", clientId);
            } catch (SQLException e) {
                log.error("Error creating session for device {}", clientId);
                log.debug("Exception: ", e);
                Services.clientService().disconnectClient(clientId).join();
            }
        } else {
            log.error("Client disconnected: No username found");
            Services.clientService().disconnectClient(clientId).join();
        }

    }

    @Override
    public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {
        final String clientId = disconnectEventInput.getClientInformation().getClientId();
        DeviceTrustAttributes device = TrustStore.remove(clientId);
        try {
            if (device != null)
                Database.updateDevice(new Device(device));
        } catch (SQLException e) {
            log.error("Error saving device {} data to database", device.getClientId());
        }
        Services.retainedMessageStore().remove(VIEW_TOPIC + clientId).join();
        log.info("Client disconnected with id: {} ", disconnectEventInput.getClientInformation().getClientId());
    }
}
