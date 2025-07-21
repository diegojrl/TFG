package trust.authorization.configuration;

public final class Rule implements Cloneable {
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

    @Override
    public Rule clone() {
        try {
            return (Rule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

