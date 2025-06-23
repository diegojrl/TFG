package client.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class File {
    public String clientId;
    public String host;
    public String username;
    public String password;
    @JsonProperty("opinions")
    public Map<String, Float> opinions;
    public List<PeriodicMessage> messages;
    public Tls tls;

    public void removeInvalidOpinions() {
        opinions.entrySet().removeIf(entry -> entry.getValue() == null || (entry.getValue() < 0 || entry.getValue() > 1));
    }

    public Float getOpinion(String clientId) {
        return opinions.get(clientId);
    }

    public static final class Tls {
        public Boolean useTls;
        public Path certificate;
        public String certificatePassword;
    }
}
