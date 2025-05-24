package org.example.configuration;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

public class Configuration {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private static int DELAY_MAX = 500;
    private static int DELAY_MIN = 20;
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

    public static void loadFromFile(Path filename) throws IOException {
        try {
            File fileConf = objectMapper.readValue(filename.toFile(), File.class);
            DELAY_MAX = fileConf.delay_max;
            DELAY_MIN = fileConf.delay_min;
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
