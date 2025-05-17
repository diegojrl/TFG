package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TrustAttributes {

    private final String clientId;
    private final AtomicInteger failureRate;
    private final AtomicLong latencySum;    //Total sum of
    private final AtomicLong latencyNum;    //Number of latency data points
    private final boolean usedTLS;
    private final boolean externalNetwork;
    private final AtomicInteger reputation;

    public TrustAttributes(String clientId, int failureRate, boolean usedTLS, boolean externalNetwork, int reputation) {
        this.clientId = clientId;
        this.failureRate = new AtomicInteger(failureRate);
        this.latencySum = new AtomicLong();
        this.latencyNum = new AtomicLong();
        this.usedTLS = usedTLS;
        this.externalNetwork = externalNetwork;
        this.reputation = new AtomicInteger(reputation);
    }


    public void setFailureRate(int failureRate) {
        this.failureRate.set(failureRate);
    }

    public void addLatency(long latency) {
        this.latencySum.addAndGet(latency);
        this.latencyNum.incrementAndGet();
    }


    public void setReputation(int reputation) {
        this.reputation.set(reputation);
    }

    public String getClientId() {
        return clientId;
    }

    public int getFailureRate() {
        return failureRate.get();
    }

    public int getLatency() {
        if (latencyNum.get() == 0) {
            return Integer.MAX_VALUE;
        }else {
            return (int)(this.latencySum.get() / latencyNum.get());
        }

    }

    public boolean isUsedTLS() {
        return usedTLS;
    }

    public boolean isExternalNetwork() {
        return externalNetwork;
    }

    public int getReputation() {
        return reputation.get();
    }


    private DeviceTrustData asDeviceTrustData() {
        Integer failureRate = this.getFailureRate();
        Integer delay = this.getLatency();
        Integer reputation = this.getReputation();
        DeviceTrustData.NetworkType networkType = this.externalNetwork ?
                DeviceTrustData.NetworkType.External : DeviceTrustData.NetworkType.Internal;
        DeviceTrustData.NetworkSecurity networkSecurity = this.usedTLS ?
                DeviceTrustData.NetworkSecurity.TLS : DeviceTrustData.NetworkSecurity.No;
        return new DeviceTrustData(failureRate,delay, networkType,networkSecurity, reputation);
    }

    public ByteBuffer encode() throws JsonProcessingException {
        return ByteBuffer.wrap(MsgTest.objectMapper.writeValueAsBytes(this.asDeviceTrustData()));
    }


}
