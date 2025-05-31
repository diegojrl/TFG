package org.example.authorization.configuration;

public class Rule {
    public Boolean allow;
    public Float trust;
    public String clientId;
    public String username;
    public Action action;
    public QoS qos;
    public Retention retention;

    @Override
    public String toString() {
        return String.format("allow: %s, trust: %f, id: %s, usn: %s, action: %s, qos: %s, ret: %s\n", allow, trust, clientId, username, action, qos, retention);
    }
}

