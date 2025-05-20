package org.example;

import java.nio.charset.StandardCharsets;
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
        if (enhancedAuthConnectInput.getConnectPacket().getUserName().isPresent()) {
            enhancedAuthOutput.authenticateSuccessfully();
        }else {
            log.info("New client: {}", clientId);
            final String user_and_passord = "user:pasw";
            enhancedAuthOutput.continueAuthentication(user_and_passord.getBytes(StandardCharsets.UTF_8));
        }
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
