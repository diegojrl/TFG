import client.Mqtt5TrustClient;
import client.configuration.ClientConfiguration;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;

import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws Exception {
        ClientConfiguration conf = loadConfig();
        MqttClientSslConfig tls = null;
        if (conf.useTls()) {
            var tlsBuilder = MqttClientSslConfig.builder();
            if (conf.getCertificate() != null) tlsBuilder = tlsBuilder.trustManagerFactory(loadCertificate(conf));
            tls = tlsBuilder.build();
        }


        final Mqtt5ClientBuilder clientBuilder = Mqtt5Client.builder()
                .sslConfig(tls)
                .serverHost(conf.getHost())
                .simpleAuth()
                .username(conf.getUsername())
                .password(conf.getPassword().getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .identifier(conf.getClientId() == null ? UUID.randomUUID().toString() : conf.getClientId())
                .automaticReconnectWithDefaultConfig();
        final Mqtt5Client client = new Mqtt5TrustClient(clientBuilder, conf);
        final Mqtt5AsyncClient asyncClient = client.toAsync();
        try {
            asyncClient.connectWith()
                    .send()
                    .get(20, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            System.err.println("Failed to connect to MQTT broker: " + e.getMessage());
            System.exit(1);
        }
        //System.exit(0);
    }


    private static ClientConfiguration loadConfig() throws IOException, URISyntaxException, NullPointerException {
        Path file;
        if (System.getenv("CONFIG_FILE") != null) {
            file = Path.of(System.getenv("CONFIG_FILE"));
        } else {
            URI fileUri = Main.class.getClassLoader().getResource("config.yaml").toURI();
            file = Path.of(fileUri);
        }
        return new ClientConfiguration(file);
    }

    private static TrustManagerFactory loadCertificate(ClientConfiguration conf) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream in = new FileInputStream(conf.getCertificate().toFile());
        trustStore.load(in, conf.getCertificatePassword().toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf;
    }
}