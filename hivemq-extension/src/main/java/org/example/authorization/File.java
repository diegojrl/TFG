package org.example.authorization;

import java.util.List;

public class File {
    enum Action {subscribe, publish, all}

    enum Retention {yes, no, any}

    public static class Permission {
        public String topic;
        public Float trust;
        public String clientId;
        public String username;
        public Action action;
        public Integer qos;
        public Retention retention;

        @Override
        public String toString() {
            return String.format("topic: %s, trust: %f, id: %s, usn: %s, action: %s, qos: %d, ret: %s", topic, trust, clientId, username, action, qos, retention);
        }
    }

    public List<Permission> permissions;

    @Override
    public String toString() {
        return permissions.toString();
    }
}
