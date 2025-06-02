package trustData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.client.parameter.Listener;
import configuration.Configuration;
import db.Database;
import db.Opinion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustControl.ControlMapper;
import trustManagement.fuzzyLogic.FuzzyCtr;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceTrustAttributes {

    private static final Logger log = LoggerFactory.getLogger(DeviceTrustAttributes.class);
    private final String clientId;
    private final AtomicLong failedPacketCount;
    private final AtomicLong totalPacketCount;
    private final AtomicLong latencySum; // Total sum of
    private final AtomicLong latencyNum; // Number of latency data points
    private boolean usedTLS;
    private boolean externalNetwork;
    private final AtomicInteger reputation; //This holds a float value

    private final AtomicInteger trustValue; //This holds a float value

    public DeviceTrustAttributes(@NotNull final ClientInformation clientInfo, @NotNull final ConnectionInformation connInfo) {
        this(clientInfo.getClientId(), isTLSUsed(connInfo), isExternalNetwork(connInfo), 0.5F);
    }

    public DeviceTrustAttributes(String clientId, long failedPacketCount, long totalPacketCount, long latencySum, long latencyNum, float reputation, float trustValue) {
        this.usedTLS = false;
        this.externalNetwork = false;
        this.clientId = clientId;
        this.failedPacketCount = new AtomicLong(failedPacketCount);
        this.totalPacketCount = new AtomicLong(totalPacketCount);
        this.latencySum = new AtomicLong(latencySum);
        this.latencyNum = new AtomicLong(latencyNum);
        this.reputation = new AtomicInteger(Float.floatToIntBits(reputation));
        this.trustValue = new AtomicInteger(Float.floatToIntBits(trustValue));
    }

    public DeviceTrustAttributes(String clientId, boolean usedTLS, boolean externalNetwork, float reputation) {
        this.clientId = clientId;
        this.failedPacketCount = new AtomicLong(1);
        this.totalPacketCount = new AtomicLong(2);
        this.latencySum = new AtomicLong();
        this.latencyNum = new AtomicLong();
        this.usedTLS = usedTLS;
        this.externalNetwork = externalNetwork;
        this.reputation = new AtomicInteger(Float.floatToIntBits(reputation));
        this.trustValue = new AtomicInteger(0);
        updateTrust();
    }

    public void addLatency(long latency) {
        this.latencySum.addAndGet(latency);
        this.latencyNum.incrementAndGet();
        updateTrust();
    }

    public void addFailedPacket() {
        this.failedPacketCount.getAndIncrement();
    }

    public void addSentPacket() {
        this.totalPacketCount.getAndIncrement();
    }

    public String getClientId() {
        return clientId;
    }

    public float getFailureRate() {
        float failureRate = ((float) failedPacketCount.get() / totalPacketCount.get());
        return Math.min(failureRate, 1F);
    }

    public void setFailureRate(int failureRate) {
        if (failureRate > 100) failureRate = 100;
        else if (failureRate < 0) failureRate = 0;
        failedPacketCount.set(failureRate);
        totalPacketCount.set(1);
        updateTrust();
    }

    public int getLatency() {
        if (latencyNum.get() == 0) {
            return Configuration.getDelayMax();
        } else {
            int latency = (int) (latencySum.get() / latencyNum.get());
            if (latency < Configuration.getDelayMin()) {
                return Configuration.getDelayMin();
            } else if (latency > Configuration.getDelayMax()) {
                return Configuration.getDelayMax();
            } else {
                return latency;
            }
        }

    }

    public float getTrustValue() {
        return Float.intBitsToFloat(trustValue.get());
    }

    public long getFailedPacketCount() {
        return failedPacketCount.get();
    }

    public long getTotalPacketCount() {
        return totalPacketCount.get();
    }

    public long getLatencySum() {
        return latencySum.get();
    }

    public long getLatencyNum() {
        return latencyNum.get();
    }

    public void updateClientInformation(ConnectionInformation connectionInformation) {
        this.usedTLS = isTLSUsed(connectionInformation);
        this.externalNetwork = isExternalNetwork(connectionInformation);
        updateTrust();
    }

    public void setLatency(int latency) {
        if (latency > Configuration.getDelayMax()) latency = Configuration.getDelayMax();
        else if (latency < Configuration.getDelayMin()) latency = Configuration.getDelayMin();
        latencySum.set(latency);
        latencyNum.set(1);
        updateTrust();
    }

    public int getSecurity() {
        if (usedTLS || !externalNetwork) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isUsedTLS() {
        return usedTLS;
    }

    public boolean isExternalNetwork() {
        return externalNetwork;
    }

    public void addOpinion(String clientId, float opinion) {
        try {
            if (Database.insertOpinion(clientId, this.clientId, opinion)) {
                updateReputation();
            }
        } catch (SQLException e) {
            log.error("Failed to add opinion", e);
        }

    }

    public float getReputation() {
        return Float.intBitsToFloat(reputation.get());
    }

    private void updateTrust() {
        try {
            float trust = (float) FuzzyCtr.getInstance().evaluate(this);
            log.trace("New trust: {}", trust);
            trustValue.set(Float.floatToIntBits(trust));
        } catch (IOException ignored) {
        }
    }

    private DeviceControlData asDeviceTrustData() {
        float failureRate = this.getFailureRate();
        int delay = this.getLatency();
        float reputation = this.getReputation();
        DeviceControlData.NetworkType networkType = this.externalNetwork ? DeviceControlData.NetworkType.External : DeviceControlData.NetworkType.Internal;
        DeviceControlData.NetworkSecurity networkSecurity = this.usedTLS ? DeviceControlData.NetworkSecurity.TLS : DeviceControlData.NetworkSecurity.No;
        float trust = Float.intBitsToFloat(trustValue.get());

        return new DeviceControlData(failureRate, delay, networkType, networkSecurity, reputation, trust);
    }

    public ByteBuffer encodeToControlFormat() throws JsonProcessingException {
        return ByteBuffer.wrap(ControlMapper.MAPPER.writeValueAsBytes(this.asDeviceTrustData()));
    }

    @Override
    public String toString() {
        return clientId + ", " + "delay: " + getLatency() + "ms, " + "fail: " + getFailureRate() + "%, " + "sec: " + (getSecurity() == 1) + ", " + "rep: " + getReputation() + "%";
    }

    private static boolean isTLSUsed(ConnectionInformation connectionInformation) {
        Optional<Listener> optionalListener = connectionInformation.getListener();
        if (optionalListener.isPresent()) {
            Listener listener = optionalListener.get();
            switch (listener.getListenerType()) {
                case TLS_TCP_LISTENER:
                case TLS_WEBSOCKET_LISTENER:
                    return true;
            }
        }
        return false;

    }

    private static boolean isExternalNetwork(ConnectionInformation connectionInformation) {
        Optional<InetAddress> optionalListener = connectionInformation.getInetAddress();
        if (optionalListener.isPresent()) {
            InetAddress address = optionalListener.get();
            return !Configuration.isTrustedNetworks(address);
        } else {
            return true;
        }
    }

    private void updateReputation() {
        try {
            List<Opinion> opinions = Database.getOpinions(clientId);
            double sum = 0;
            for (Opinion o : opinions) {
                DeviceTrustAttributes dev = TrustStore.get(o.sourceId);
                float rep = dev == null ? o.trust : dev.getTrustValue();
                sum += rep * o.opinion;
                log.trace("{} opinion {} * {} reputation", o.sourceId, rep, o.opinion);
            }
            float newRep = (float) (sum / opinions.size());
            log.info("New reputation: {}", newRep);
            this.reputation.set(Float.floatToIntBits(newRep));
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

}
