package trust.authorization;

import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.RetainHandling;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import trust.trustData.DeviceTrustAttributes;
import trust.trustData.TrustStore;

public class PolicyInformationPoint {

    public AuthzData getAuthzData(ClientInformation clientInfo, ConnectionInformation conInfo, Subscription sub) {
        final String clientId = clientInfo.getClientId();
        final DeviceTrustAttributes dev = TrustStore.get(clientId);
        if (dev == null)
            return null;
        final String username = conInfo.getConnectionAttributeStore().getAsString("username").orElse("");
        final int qos = sub.getQos().getQosNumber();
        final boolean retain = sub.getRetainHandling() != RetainHandling.DO_NOT_SEND;
        final float trust = dev.getTrustValue();

        return new AuthzData(clientId, trust, username, qos, retain);
    }

    public AuthzData getAuthzData(ClientInformation clientInfo, ConnectionInformation conInfo, PublishPacket pub) {
        final String clientId = clientInfo.getClientId();
        final DeviceTrustAttributes dev = TrustStore.get(clientId);
        if (dev == null)
            return null;
        final String username = conInfo.getConnectionAttributeStore().getAsString("username").orElse("");
        final int qos = pub.getQos().getQosNumber();
        final boolean retain = pub.getRetain();
        final float trust = dev.getTrustValue();

        return new AuthzData(clientId, trust, username, qos, retain);
    }

    public record AuthzData(String clientId, float trust, String username, int qos, boolean retain) {
    }


}
