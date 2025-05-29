package org.example.authorization;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.auth.Authorizer;
import com.hivemq.extension.sdk.api.auth.parameter.AuthorizerProviderInput;
import com.hivemq.extension.sdk.api.services.auth.provider.AuthorizerProvider;
import org.example.trustData.DeviceTrustAttributes;
import org.example.trustData.TrustStore;

public class AuthzInterceptor implements AuthorizerProvider {
    @Override
    public @Nullable Authorizer getAuthorizer(@NotNull AuthorizerProviderInput authorizerProviderInput) {
        final String clientId = authorizerProviderInput.getClientInformation().getClientId();
        final DeviceTrustAttributes trust = TrustStore.get(clientId);
        return null;
    }
}
