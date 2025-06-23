package client;

import client.configuration.Messages;
import client.configuration.Opinions;
import com.hivemq.client.mqtt.MqttClientState;
import com.hivemq.client.mqtt.mqtt5.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Mqtt5TrustClient implements Mqtt5Client {
    private final Mqtt5Client client;

    public Mqtt5TrustClient(String host, int port, Opinions opinions, Messages messages) {
        this(Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverAddress(InetSocketAddress.createUnresolved(host, port))
                .automaticReconnect()
                .applyAutomaticReconnect(), opinions, messages);
    }

    public Mqtt5TrustClient(Mqtt5ClientBuilder clientBuilder, Opinions opinions, Messages messages) {
        final Mqtt5TrustConnectionLifeCycle connectionLifeCycle = new Mqtt5TrustConnectionLifeCycle();
        client = clientBuilder
                .addConnectedListener(connectionLifeCycle)
                .addDisconnectedListener(connectionLifeCycle)
                .build();
        connectionLifeCycle.setClient(client);
        connectionLifeCycle.setOpinions(opinions);
        connectionLifeCycle.addPeriodicMessages(messages);
    }

    @Override
    public @NotNull Mqtt5ClientConfig getConfig() {
        return client.getConfig();
    }

    @Override
    public @NotNull MqttClientState getState() {
        return Mqtt5Client.super.getState();
    }

    @Override
    public @NotNull Mqtt5RxClient toRx() {
        return client.toRx();
    }

    @Override
    public @NotNull Mqtt5AsyncClient toAsync() {
        return client.toAsync();
    }

    @Override
    public @NotNull Mqtt5BlockingClient toBlocking() {
        return client.toBlocking();
    }

}
