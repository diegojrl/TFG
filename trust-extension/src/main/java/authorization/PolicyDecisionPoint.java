package authorization;

import authorization.configuration.Action;
import authorization.configuration.QoS;
import authorization.configuration.Retention;
import authorization.configuration.Rule;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class PolicyDecisionPoint {
    private static final Logger log = LoggerFactory.getLogger(PolicyDecisionPoint.class);

    private static PolicyDecisionPoint instance;

    private final PolicyAdministrationPoint pap;
    private final PolicyInformationPoint pip;

    private PolicyDecisionPoint() throws IOException {
        Path policyPath = Configuration.getConfigDir().resolve("accessControl.yaml");
        this.pap = new PolicyAdministrationPoint(policyPath);
        this.pip = new PolicyInformationPoint();
        instance = this;
    }

    public static PolicyDecisionPoint getInstance() throws IOException {
        if (instance == null) {
            instance = new PolicyDecisionPoint();
        }
        return instance;
    }

    public boolean authorizeSubscription(ClientInformation clientInfo, ConnectionInformation conInfo, Subscription sub) {
        final PolicyInformationPoint.AuthzData data = pip.getAuthzData(clientInfo, conInfo, sub);

        List<Rule> rules = pap.getPolicy(sub.getTopicFilter(), data.clientId(), data.username());


        for (Rule rule : rules)
            if (matchRule(rule, Action.subscribe, data.clientId(), data.username(), data.qos(), data.retain(), data.trust()))
                return rule.allow;

        return false;
    }

    public boolean authorizePublish(ClientInformation clientInfo, ConnectionInformation conInfo, PublishPacket pub) {
        final PolicyInformationPoint.AuthzData data = pip.getAuthzData(clientInfo, conInfo, pub);

        List<Rule> rules = pap.getPolicy(pub.getTopic(), data.clientId(), data.username());

        for (Rule rule : rules)
            if (matchRule(rule, Action.publish, data.clientId(), data.username(), data.qos(), data.retain(), data.trust()))
                return rule.allow;

        return false;
    }

    private static boolean matchRule(Rule rule, Action action, String clientId, String username, int qos, boolean retain, float trust) {
        log.debug("Matching rule: {}", rule);
        if (rule.action != Action.all && rule.action != action) return false;
        else if (rule.clientId != null && !rule.clientId.equals(clientId)) return false;
        else if (rule.username != null && !rule.username.equals(username)) return false;
        else if (!matchQoS(rule.qos, qos)) return false;
        else if (!matchRetention(rule.retention, retain)) return false;
        else if (rule.trust != null) return rule.allow ? trust > rule.trust : trust < rule.trust;
        else return true;
    }

    private static boolean matchQoS(QoS ruleQoS, int qos) {
        if (ruleQoS == QoS.any) return true;
        else if (qos == 0) return ruleQoS == QoS.zero || ruleQoS == QoS.zero_one || ruleQoS == QoS.zero_two;
        else if (qos == 1) return ruleQoS == QoS.one || ruleQoS == QoS.zero_one || ruleQoS == QoS.one_two;
        else if (qos == 2) return ruleQoS == QoS.two || ruleQoS == QoS.zero_two || ruleQoS == QoS.one_two;
        else return false;
    }

    private static boolean matchRetention(Retention ruleRetention, boolean retention) {
        if (ruleRetention == Retention.any) return true;
        else if (ruleRetention == Retention.no && !retention) return true;
        else return ruleRetention == Retention.yes && retention;
    }
}
