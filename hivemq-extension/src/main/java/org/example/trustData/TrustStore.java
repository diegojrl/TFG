package org.example.trustData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrustStore {
    private static final Map<String, DeviceTrustAttributes> trustStore = new ConcurrentHashMap<>();
    private TrustStore() {}
    public static DeviceTrustAttributes getTrustAttributes(String clientId) {
        return trustStore.get(clientId);
    }
    public static void putTrustAttributes(String clientId, DeviceTrustAttributes trustAttributes) {
        trustStore.put(clientId, trustAttributes);
    }
}
