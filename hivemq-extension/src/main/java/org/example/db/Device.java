package org.example.db;


public class Device {
    public final int userId;
    public final String clientId;
    public final int latencySum;
    public final int latencyCnt;
    public final int MessageFailed;

    public Device(int userId, String clientId, int latencySum, int latencyCnt, int messageFailed, int messageCnt, float reputation) {
        this.userId = userId;
        this.clientId = clientId;
        this.latencySum = latencySum;
        this.latencyCnt = latencyCnt;
        MessageFailed = messageFailed;
        MessageCnt = messageCnt;
        this.reputation = reputation;
    }

    public final int MessageCnt;
    public final float reputation;



    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Device)) return false;

        Device device = (Device) o;
        return userId == device.userId && clientId.equals(device.clientId);
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + clientId.hashCode();
        return result;
    }


}
