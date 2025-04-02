package org.example;

public class TrustAttributes {
    private final String clientId;
    private int failureRate;
    private long latencySum;    //Total sum of
    private long latencyNum;    //Number of latency data points
    private boolean usedTLS;
    private boolean externalNetwork;
    private int reputation;

    public TrustAttributes(String clientId, int failureRate, boolean usedTLS, boolean externalNetwork, int reputation) {
        this.clientId = clientId;
        this.failureRate = failureRate;
        this.latencySum = 0;
        this.latencyNum = 0;
        this.usedTLS = usedTLS;
        this.externalNetwork = externalNetwork;
        this.reputation = reputation;
    }

    public TrustAttributes(String clientId) {
        this.clientId = clientId;
    }

    public void setFailureRate(int failureRate) {
        this.failureRate = failureRate;
    }

    public void addLatency(long latency) {
        this.latencySum += latency;
        this.latencyNum++;
    }

    public void setUsedTLS(boolean usedTLS) {
        this.usedTLS = usedTLS;
    }

    public void setExternalNetwork(boolean externalNetwork) {
        this.externalNetwork = externalNetwork;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getClientId() {
        return clientId;
    }

    public int getFailureRate() {
        return failureRate;
    }

    public long getLatency() {
        if (latencyNum == 0) {
            return Integer.MAX_VALUE;
        }else {
            return this.latencySum / latencyNum;
        }

    }

    public boolean isUsedTLS() {
        return usedTLS;
    }

    public boolean isExternalNetwork() {
        return externalNetwork;
    }

    public int getReputation() {
        return reputation;
    }


}
