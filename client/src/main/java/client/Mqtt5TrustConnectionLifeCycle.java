package client;

import client.configuration.Opinions;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedContext;
import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedListener;
import com.hivemq.client.mqtt.lifecycle.MqttClientDisconnectedContext;
import com.hivemq.client.mqtt.lifecycle.MqttClientDisconnectedListener;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.concurrent.*;

public class Mqtt5TrustConnectionLifeCycle implements MqttClientConnectedListener, MqttClientDisconnectedListener {
    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private Mqtt5AsyncClient client;
    private Opinions opinions;

    public void setClient(Mqtt5Client client) {
        this.client = client.toAsync();
    }

    public void setOpinions(Opinions opinions) {
        this.opinions = opinions;
    }

    @Override
    public void onConnected(@NotNull MqttClientConnectedContext mqttClientConnectedContext) {
        try {
            Mqtt5Subscribe sub = Mqtt5Subscribe.builder()
                    .topicFilter(Topics.PING)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .build();
            client.subscribe(sub, (a) -> {
            }, executor);
            sub = Mqtt5Subscribe.builder()
                    .topicFilter(Topics.OPINIONS + '+')
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .build();
            client.subscribe(sub, (a) -> {
                final String topic = a.getTopic().toString();
                int idx = topic.lastIndexOf('/') + 1;
                if (idx != 0) {
                    Float opinion = opinions.getOpinion(topic.substring(idx));
                    if (opinion != null) {
                        System.err.println(topic.substring(idx) + ": " + opinion);
                        final ByteBuffer payload = ByteBuffer.allocate(Float.BYTES);
                        payload.putFloat(opinion);
                        payload.position(0);
                        try {
                            client.publishWith()
                                    .topic(topic)
                                    .qos(MqttQos.AT_LEAST_ONCE)
                                    .payload(payload)
                                    .send()
                                    .get(5, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            System.err.println("Error publishing opinion: " + e.getLocalizedMessage());
                        }
                    }
                }

            }, executor).whenCompleteAsync((a, e) -> {
                if (e == null) {
                    for (var ack : a.getReasonCodes()) {
                        if (ack.isError())
                            System.err.println(ack);
                    }
                } else {
                    System.err.println(e.getLocalizedMessage());
                }
                System.err.println("subs ok");
            }, executor);
        } catch (Exception e) {
            System.out.println("Subscription error: " + e.getLocalizedMessage());
        }

    }

    @Override
    public void onDisconnected(@NotNull MqttClientDisconnectedContext mqttClientDisconnectedContext) {
        System.err.println("Client disconnected");
        mqttClientDisconnectedContext.getCause().printStackTrace();
    }
}
