package client.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ClientConfiguration implements Opinions, Messages {
    private final File configFile;

    public ClientConfiguration(Path file) throws IOException {
        ObjectMapper om = new YAMLMapper();
        this.configFile = om.readValue(file.toFile(), File.class);
        if (this.configFile != null) this.configFile.removeInvalidOpinions();
    }

    public String getClientId() {
        return configFile.clientId;
    }

    public String getHost() {
        return configFile.host;
    }

    public String getUsername() {
        return configFile.username;
    }

    public String getPassword() {
        return configFile.password;
    }

    public boolean useTls() {
        if (configFile.tls != null) {
            return configFile.tls.useTls == null || configFile.tls.useTls;
        } else {
            return true;
        }
    }

    public Path getCertificate() {
        return configFile.tls != null ? configFile.tls.certificate : null;
    }

    public String getCertificatePassword() {
        return configFile.tls != null ? configFile.tls.certificatePassword : null;
    }

    public Float getOpinion(String clientId) {
        return configFile.getOpinion(clientId);
    }

    public List<PeriodicMessage> getPeriodicMessages() {
        return configFile.messages;
    }
}
