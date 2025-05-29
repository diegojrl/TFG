package org.example.authentication;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.async.TimeoutFallback;
import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthOutput;
import com.hivemq.extension.sdk.api.packets.connect.ConnackReasonCode;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import org.example.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

public class Auth implements SimpleAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);
    private final ManagedExtensionExecutorService executor = Services.extensionExecutorService();
    private final Properties env = new Properties(5);

    public Auth() {
        //https://www.baeldung.com/java-ldap-auth
        this.env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        this.env.put(Context.PROVIDER_URL, Configuration.getLdapUrl());
        this.env.put(Context.SECURITY_AUTHENTICATION, Configuration.getLdapAuth());
    }

    @Override
    public void onConnect(@NotNull SimpleAuthInput simpleAuthInput, @NotNull SimpleAuthOutput simpleAuthOutput) {
        final Optional<String> username = simpleAuthInput.getConnectPacket().getUserName();
        final Optional<ByteBuffer> password = simpleAuthInput.getConnectPacket().getPassword();

        if (username.isPresent() && password.isPresent()) {

            simpleAuthInput.getConnectionInformation().getConnectionAttributeStore().putAsString("username", username.get());

            final var output = simpleAuthOutput.async(Duration.ofSeconds(10), TimeoutFallback.FAILURE);
            executor.submit(() -> {
                final String passw = StandardCharsets.UTF_8.decode(password.get()).toString();

                if (authenticate(username.get(), passw)) {
                    output.getOutput().authenticateSuccessfully();
                } else {
                    output.getOutput().failAuthentication(ConnackReasonCode.UNSPECIFIED_ERROR);
                }
                output.resume();
            });

        } else {

            simpleAuthOutput.failAuthentication(ConnackReasonCode.BAD_USER_NAME_OR_PASSWORD, "No username or password");

        }
    }
    private boolean authenticate(final String username, final String password)  {
        Properties env = (Properties) this.env.clone();
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + "," + Configuration.getLdapBaseDn());
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            DirContext dir = new InitialDirContext(env);
            dir.close();
            return true;
        } catch (NamingException e) {
            log.error("Auth threw and exception", e);
            return false;
        }
    }

}
