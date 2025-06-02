
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

import authentication.LdapAuth;
import authorization.PolicyEnforcementPoint;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.Authorizer;
import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.events.EventRegistry;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.auth.SecurityRegistry;
import com.hivemq.extension.sdk.api.services.intializer.InitializerRegistry;
import configuration.Configuration;
import db.Database;
import fuzzyLogic.FuzzyCtr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustControl.ControlSub;
import trustControl.ModificationListener;
import trustManagement.PingInterceptor;
import trustManagement.PublishOutboundChecks;
import trustManagement.ReputationListener;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * This is the main class of the extension,
 * which is instantiated either during the HiveMQ start up process (if extension is enabled)
 * or when HiveMQ is already started by enabling the extension.
 *
 * @author Florian Limpöck
 * @since 4.0.0
 */
public class ExtensionMain implements com.hivemq.extension.sdk.api.ExtensionMain {
    private static final @NotNull Logger log = LoggerFactory.getLogger(ExtensionMain.class);

    @Override
    public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput, final @NotNull ExtensionStartOutput extensionStartOutput) {
        final Path dataPath = extensionStartInput.getServerInformation().getDataFolder().toPath();
        final ExtensionInformation extensionInformation = extensionStartInput.getExtensionInformation();
        try {
            //Load config from file
            Configuration.setFolder(extensionInformation.getExtensionHomeFolder());

            Database.init(dataPath);
            //Init the fuzzy controller
            FuzzyCtr.getInstance();
            addAuthn();
            addAuthz();
            addClientLifecycleEventListener();
            addPublishModifier();
            //Update control info every 5s
            Services.extensionExecutorService().scheduleAtFixedRate(ControlSub.controlTask, 2, ControlSub.RUN_INTERVAL_SEC, TimeUnit.SECONDS);
            log.info("Started {}:{}", extensionInformation.getName(), extensionInformation.getVersion());
        } catch (IOException e) {
            log.error("Failed to read configuration", e);
            extensionStartOutput.preventExtensionStartup("Failed to read configuration: " + e.getMessage());
        } catch (SQLException e) {
            log.error("Error starting database ", e);
            extensionStartOutput.preventExtensionStartup(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error("Exception thrown at extension start: ", e);
            extensionStartOutput.preventExtensionStartup(e.getLocalizedMessage());
        }
    }

    @Override
    public void extensionStop(final @NotNull ExtensionStopInput extensionStopInput, final @NotNull ExtensionStopOutput extensionStopOutput) {

        final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
        log.info("Stopped {}:{}", extensionInformation.getName(), extensionInformation.getVersion());

    }

    private void addAuthn() {
        final SecurityRegistry securityRegistry = Services.securityRegistry();
        final SimpleAuthenticator authn = new LdapAuth();
        securityRegistry.setAuthenticatorProvider(in -> authn);
    }

    private void addAuthz() throws IOException {
        final SecurityRegistry securityRegistry = Services.securityRegistry();
        final Authorizer authz = new PolicyEnforcementPoint();
        securityRegistry.setAuthorizerProvider(in -> authz);
        final InitializerRegistry initializerRegistry = Services.initializerRegistry();
        log.info("Added authorizer");
    }

    private void addClientLifecycleEventListener() {

        final EventRegistry eventRegistry = Services.eventRegistry();

        final LifeCycleListener lifeCycleListener = new LifeCycleListener();

        eventRegistry.setClientLifecycleEventListener(input -> lifeCycleListener);

    }

    private void addPublishModifier() {
        final InitializerRegistry initializerRegistry = Services.initializerRegistry();

        final PingInterceptor ping = new PingInterceptor();
        final PublishOutboundChecks outChecks = new PublishOutboundChecks();
        final ModificationListener modListener = new ModificationListener();
        final ReputationListener reputation = new ReputationListener();
        initializerRegistry.setClientInitializer((initializerInput, clientContext) -> {

            //Set up outbound checks
            clientContext.addPublishOutboundInterceptor(outChecks);
            clientContext.addPubrecOutboundInterceptor(outChecks);

            //Set up Ping
            clientContext.addPingReqInboundInterceptor(ping);
            clientContext.addPubackInboundInterceptor(ping);
            clientContext.addPubrecInboundInterceptor(ping);
            clientContext.addPubrelInboundInterceptor(ping);

            //Set up control mod listener
            clientContext.addPublishInboundInterceptor(modListener);

            //Set up reputation listener
            clientContext.addPublishInboundInterceptor(reputation);
            clientContext.addSubscribeInboundInterceptor(reputation);
        });
    }

}
