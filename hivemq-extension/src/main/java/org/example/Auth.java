package org.example;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.EnhancedAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthConnectInput;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.EnhancedAuthOutput;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.client.parameter.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Auth implements EnhancedAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);

    @Override
    public void onConnect(@NotNull EnhancedAuthConnectInput enhancedAuthConnectInput, @NotNull EnhancedAuthOutput enhancedAuthOutput) {
        String clientId = enhancedAuthConnectInput.getClientInformation().getClientId();
        boolean usedTLS = isTLSUsed(enhancedAuthConnectInput.getConnectionInformation());
        boolean externalNetwork = isExternalNetwork(enhancedAuthConnectInput.getConnectionInformation());
        TrustStore.putTrustAttributes(clientId, new TrustAttributes(clientId,0,usedTLS,externalNetwork,50));
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


    private boolean isTLSUsed(ConnectionInformation connectionInformation) {
        Optional<Listener> optionalListener = connectionInformation.getListener();
        if (optionalListener.isPresent()) {
            Listener listener = optionalListener.get();
            switch (listener.getListenerType()) {
                case TLS_TCP_LISTENER:
                case TLS_WEBSOCKET_LISTENER:
                    return true;
                default:
                    //Connection is considered 'Secured' if its localhost
                    Optional<InetAddress> optionalInetAddress = connectionInformation.getInetAddress();
                    if (optionalInetAddress.isPresent()) {
                        InetAddress address = optionalInetAddress.get();
                        return address.isLoopbackAddress();
                    }else {
                        return false;
                    }
            }
        }else {
            return false;
        }
    }
    private boolean isExternalNetwork(ConnectionInformation connectionInformation) {
        Optional<InetAddress> optionalListener = connectionInformation.getInetAddress();
        if (optionalListener.isPresent()) {
            InetAddress address = optionalListener.get();
            return !address.isAnyLocalAddress();
        }else {
            return true;
        }
    }
}
