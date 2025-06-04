package authorization;

import authorization.configuration.*;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PolicyAdministrationPoint {
    private static final Logger log = LoggerFactory.getLogger(PolicyAdministrationPoint.class);
    private static final String CLIENTID_SUBSTITUTION = "\\$\\{\\{clientid}}";
    private static final String USERNAME_SUBSTITUTION = "\\$\\{\\{username}}";
    private final List<Policy> policies;

    public PolicyAdministrationPoint(Path file) throws IOException {
        try {
            ObjectMapper objectMapper = new YAMLMapper();
            File policies = objectMapper.readValue(file.toFile(), File.class);
            this.policies = policies.permissions;
            //Enforce default values
            for (Policy p : this.policies) {
                if (p.topic == null || p.topic.isBlank()) throw new IOException("Policy topic is empty or not present");
                for (Rule rule : p.rules) {
                    if (rule.allow == null) rule.allow = true;
                    if (rule.action == null) rule.action = Action.all;
                    if (rule.qos == null) rule.qos = QoS.any;
                    if (rule.retention == null) rule.retention = Retention.any;
                }
            }
            log.debug("PAP created: {}", policies);
        } catch (StreamReadException | DatabindException | NullPointerException e) {
            throw new IOException(e);
        }

    }

    private static boolean matches(final Policy policy, final String messageTopic, final String clientId, final String username) {
        String policyTopic = policy.topic;

        //Substitutions for clientId & username
        policyTopic = policyTopic.replaceAll(USERNAME_SUBSTITUTION, username);
        policyTopic = policyTopic.replaceAll(CLIENTID_SUBSTITUTION, clientId);

        return MqttTopicFilter.of(policyTopic).matches(MqttTopicFilter.of(messageTopic));
    }

    /**
     * @param topic HiveMQ topic filter
     * @return List with the rules matching the topic, might be empty
     */
    public List<Rule> getPolicy(final String topic, final String clientId, final String username) {
        List<Rule> rules = new ArrayList<>();
        for (Policy policy : policies)
            if (matches(policy, topic, clientId, username)) rules.addAll(policy.rules);
        return rules;
    }
}
