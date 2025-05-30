package org.example.trustManagement;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;

public class ReputationInterceptor implements ClientLifecycleEventListener {
    @Override
    public void onMqttConnectionStart(@NotNull ConnectionStartInput connectionStartInput) {
    }

    @Override
    public void onAuthenticationSuccessful(@NotNull AuthenticationSuccessfulInput authenticationSuccessfulInput) {
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEventInput disconnectEventInput) {
    }
}
