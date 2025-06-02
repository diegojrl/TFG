package configuration;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Path configDir;
    private static int DELAY_MAX = 500;
    private static int DELAY_MIN = 20;
    private static File.Ldap ldap;
    private static Network[] trustedNetworks;

    public static int getDelayMax() {
        return DELAY_MAX;
    }

    public static int getDelayMin() {
        return DELAY_MIN;
    }

    public static boolean isTrustedNetworks(@NotNull InetAddress address) {
        for (Network network : trustedNetworks)
            if (network.matches(address)) return true;
        return false;
    }

    public static String getLdapUrl() {
        return ldap.url;
    }

    public static String getLdapAuth() {
        return ldap.auth;
    }

    public static String getLdapBaseDn() {
        return ldap.baseDn;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static void setFolder(java.io.File folder) throws IOException {
        configDir = folder.toPath().resolve("conf");
        loadFromFile(configDir.resolve("config.yaml"));
    }

    private static void loadFromFile(Path filename) throws IOException {
        try {
            ObjectMapper objectMapper = new YAMLMapper();
            File fileConf = objectMapper.readValue(filename.toFile(), File.class);
            DELAY_MAX = fileConf.delay_max;
            DELAY_MIN = fileConf.delay_min;
            ldap = fileConf.ldap;
            trustedNetworks = new Network[fileConf.trusted_networks.length];
            int idx = 0;
            for (String net : fileConf.trusted_networks) {
                trustedNetworks[idx++] = new Network(net);
            }
            log.info("Loaded trust config");
        } catch (DatabindException | StreamReadException | UnknownHostException e) {
            throw new IOException(e);
        }

    }

}
