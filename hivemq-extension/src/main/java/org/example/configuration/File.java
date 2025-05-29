package org.example.configuration;

public final class File {
    public int delay_max;
    public int delay_min;
    public String[] trusted_networks;
    public Ldap ldap;

    public static class Ldap {
        public String url;
        public String baseDn;
        public String auth;
    }
}
