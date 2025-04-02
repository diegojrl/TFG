package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrustStore {
    private static final Map<String, TrustAttributes> trustStore = new ConcurrentHashMap<>();
    private TrustStore() {}
    public static TrustAttributes getTrustAttributes(String clientId) {
        return trustStore.get(clientId);
    }
    public static void putTrustAttributes(String clientId, TrustAttributes trustAttributes) {
        trustStore.put(clientId, trustAttributes);
    }
}
