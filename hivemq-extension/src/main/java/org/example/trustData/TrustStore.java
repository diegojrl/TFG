package org.example.trustData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrustStore {

    private static final Map<String, DeviceTrustAttributes> trustStore = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(TrustStore.class);

    private TrustStore() {}
    public static DeviceTrustAttributes get(final String clientId) {
        return trustStore.get(clientId);
    }
    public static void put(final String clientId, final DeviceTrustAttributes trustAttributes) {
        trustStore.put(clientId, trustAttributes);
    }
    public static void remove(final String clientId) {
        trustStore.remove(clientId);
    }


}
