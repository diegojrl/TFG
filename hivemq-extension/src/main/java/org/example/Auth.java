package org.example;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.EnhancedAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthConnectInput;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Auth implements EnhancedAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);

    @Override
    public void onConnect(@NotNull EnhancedAuthConnectInput enhancedAuthConnectInput, @NotNull EnhancedAuthOutput enhancedAuthOutput) {
        String clientId = enhancedAuthConnectInput.getClientInformation().getClientId();
        log.info("New client: {}", clientId);
        enhancedAuthOutput.authenticateSuccessfully();
    }

    @Override
    public void onReAuth(@NotNull EnhancedAuthInput enhancedAuthInput, @NotNull EnhancedAuthOutput enhancedAuthOutput) {
        EnhancedAuthenticator.super.onReAuth(enhancedAuthInput, enhancedAuthOutput);
    }

    @Override
    public void onAuth(@NotNull EnhancedAuthInput enhancedAuthInput, @NotNull EnhancedAuthOutput enhancedAuthOutput) {
        enhancedAuthOutput.authenticateSuccessfully();
    }
}
