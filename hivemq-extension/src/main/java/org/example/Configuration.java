package org.example;

public class Configuration {
    private static int DELAY_MAX = 500;
    private static int DELAY_MIN = 20;

    public static int getDelayMax() {
        return DELAY_MAX;
    }
    public static int getDelayMin() {
        return DELAY_MIN;
    }
}
