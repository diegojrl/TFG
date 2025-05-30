package org.example.authorization;

import java.util.List;

public class File {
    enum Action {SUBSCRIBE, PUBLISH, ALL}

    enum Retention {YES, NO, ANY}

    public static class Permission {
        public String topic;
        public float trust;
        public String clientId;
        public String username;
        public Action action;
        public int qos;
        public Retention retention;
    }

    public List<Permission> permissions;
}
