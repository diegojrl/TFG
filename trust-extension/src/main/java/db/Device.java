package db;


import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import trustData.DeviceTrustAttributes;

public class Device {
    public final Integer userId;
    public final String clientId;
    public final long latencySum;
    public final long latencyCnt;
    public final long messageFailed;
    public final long messageCnt;
    public final float reputation;
    public final float trust;

    public Device(int userId, String clientId, long latencySum, long latencyCnt, long messageFailed, long messageCnt, float reputation, float trust) {
        this.userId = userId;
        this.clientId = clientId;
        this.latencySum = latencySum;
        this.latencyCnt = latencyCnt;
        this.messageFailed = messageFailed;
        this.messageCnt = messageCnt;
        this.reputation = reputation;
        this.trust = trust;
    }

    public Device(DeviceTrustAttributes trustAttributes) {
        this(trustAttributes, null);
    }

    public Device(DeviceTrustAttributes trustAttributes, Integer userId) {
        this.userId = userId;
        this.clientId = trustAttributes.getClientId();
        this.latencySum = trustAttributes.getLatencySum();
        this.latencyCnt = trustAttributes.getLatencyNum();
        this.messageFailed = trustAttributes.getFailedPacketCount();
        this.messageCnt = trustAttributes.getTotalPacketCount();
        this.reputation = trustAttributes.getReputation();
        this.trust = trustAttributes.getTrustValue();
    }

    public DeviceTrustAttributes toTrustAttributes(ConnectionInformation connectionInformation) {
        DeviceTrustAttributes device = new DeviceTrustAttributes(clientId, messageFailed, messageCnt, latencySum, latencyCnt, reputation, trust);
        device.updateClientInformation(connectionInformation);
        return device;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Device device)) return false;

        return userId.equals(device.userId) && clientId.equals(device.clientId);
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + clientId.hashCode();
        return result;
    }


}
