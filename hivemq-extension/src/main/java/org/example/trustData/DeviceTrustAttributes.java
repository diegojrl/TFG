package org.example.trustData;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.trustControl.ControlMapper;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceTrustAttributes {
    public static int MIN_LATENCY = 20;
    public static int MAX_LATENCY = 500;

    private final String clientId;
    private final AtomicLong failedPacketCount;
    private final AtomicLong totalPacketCount;
    private final AtomicLong latencySum; // Total sum of
    private final AtomicLong latencyNum; // Number of latency data points
    private final boolean usedTLS;
    private final boolean externalNetwork;
    private final AtomicInteger reputation;

    public DeviceTrustAttributes(String clientId, boolean usedTLS, boolean externalNetwork, int reputation) {
        this.clientId = clientId;
        this.failedPacketCount = new AtomicLong(1);
        this.totalPacketCount = new AtomicLong(2);
        this.latencySum = new AtomicLong();
        this.latencyNum = new AtomicLong();
        this.usedTLS = usedTLS;
        this.externalNetwork = externalNetwork;
        this.reputation = new AtomicInteger(reputation);
    }

    public void addLatency(long latency) {
        this.latencySum.addAndGet(latency);
        this.latencyNum.incrementAndGet();
    }

    public void addFailedPacket() {
        this.failedPacketCount.getAndIncrement();
    }

    public void addSentPacket() {
        this.totalPacketCount.getAndIncrement();
    }

    public void setReputation(int reputation) {
        this.reputation.set(reputation);
    }

    public String getClientId() {
        return clientId;
    }

    public double getFailureRate() {
        double failureRate = ((double) failedPacketCount.get() / totalPacketCount.get());
        return Math.min(failureRate, 1);
    }

    public int getLatency() {
        if (latencyNum.get() == 0) {
            return MAX_LATENCY;
        } else {
            int latency = (int) (latencyNum.get() / latencySum.get());
            if (latency < MIN_LATENCY) {
                return MIN_LATENCY;
            }else if (latency > MAX_LATENCY) {
                return MAX_LATENCY;
            }else {
                return latency;
            }
        }

    }

    public int getSecurity() {
        if (usedTLS || !externalNetwork) {
            return 1;
        }else {
            return 0;
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

    private DeviceControlData asDeviceTrustData() {
        Double failureRate = this.getFailureRate();
        Integer delay = this.getLatency();
        Integer reputation = this.getReputation();
        DeviceControlData.NetworkType networkType = this.externalNetwork ? DeviceControlData.NetworkType.External
                : DeviceControlData.NetworkType.Internal;
        DeviceControlData.NetworkSecurity networkSecurity = this.usedTLS ? DeviceControlData.NetworkSecurity.TLS
                : DeviceControlData.NetworkSecurity.No;
        return new DeviceControlData(failureRate, delay, networkType, networkSecurity, reputation);
    }

    public ByteBuffer encodeToControlFormat() throws JsonProcessingException {
        return ByteBuffer.wrap(ControlMapper.MAPPER.writeValueAsBytes(this.asDeviceTrustData()));
    }

}
