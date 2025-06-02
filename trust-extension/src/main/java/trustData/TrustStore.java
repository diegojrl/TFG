package trustData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TrustStore {
    private static final Map<String, DeviceTrustAttributes> trustStore = new ConcurrentHashMap<>();

    private TrustStore() {
    }

    public static DeviceTrustAttributes get(final String clientId) {
        return trustStore.get(clientId);
    }

    public static void put(final String clientId, final DeviceTrustAttributes trustAttributes) {
        trustStore.put(clientId, trustAttributes);
    }

    public static DeviceTrustAttributes remove(final String clientId) {
        return trustStore.remove(clientId);
    }


}
