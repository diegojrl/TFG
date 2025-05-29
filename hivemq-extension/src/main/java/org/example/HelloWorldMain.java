
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

package org.example;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.events.EventRegistry;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.auth.SecurityRegistry;
import com.hivemq.extension.sdk.api.services.intializer.InitializerRegistry;
import org.example.authentication.Auth;
import org.example.configuration.Configuration;
import org.example.db.Database;
import org.example.trustControl.ControlSub;
import org.example.trustControl.ModificationListener;
import org.example.trustManagement.PingInterceptor;
import org.example.trustManagement.PublishOutboundChecks;
import org.example.trustManagement.TrustEvalInterceptor;
import org.example.trustManagement.fuzzyLogic.FuzzyCtr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class HelloWorldMain implements ExtensionMain {
    private static final @NotNull Logger log = LoggerFactory.getLogger(HelloWorldMain.class);
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
            addAuth();
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

    private void addAuth() {
        final SecurityRegistry securityRegistry = Services.securityRegistry();
        final SimpleAuthenticator auth = new Auth();
        securityRegistry.setAuthenticatorProvider(in -> auth);
    }

    private void addClientLifecycleEventListener() {

        final EventRegistry eventRegistry = Services.eventRegistry();

        final HelloWorldListener helloWorldListener = new HelloWorldListener();

        eventRegistry.setClientLifecycleEventListener(input -> helloWorldListener);

    }

    private void addPublishModifier() {
        final InitializerRegistry initializerRegistry = Services.initializerRegistry();

        final TrustEvalInterceptor trustEvalInterceptor = new TrustEvalInterceptor();
        final PingInterceptor ping = new PingInterceptor();
        final PublishOutboundChecks outChecks = new PublishOutboundChecks();
        final ModificationListener modListener = new ModificationListener();

        initializerRegistry.setClientInitializer((initializerInput, clientContext) -> {

            //Setup Authorization checks
            clientContext.addPublishInboundInterceptor(trustEvalInterceptor);
            clientContext.addPublishOutboundInterceptor(trustEvalInterceptor);


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

        });
    }

}
