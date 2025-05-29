package org.example.trustData;

public class DeviceControlData {
    public enum NetworkType {Internal, External}
    public enum NetworkSecurity { No, TLS }


    public float failedInteractionsPercentage;
    public int avgDelay;
    public NetworkType networkType;  //One of "Internal" | "External"
    public NetworkSecurity networkSecurity;
    public int reputation;
    public float trustValue;

    public DeviceControlData(float failedInteractionsPercentage, int avgDelay, NetworkType networkType, NetworkSecurity networkSecurity, int reputation, float trustValue) {
        this.failedInteractionsPercentage = failedInteractionsPercentage;
        this.avgDelay = avgDelay;
        this.networkType = networkType;
        this.networkSecurity = networkSecurity;
        this.reputation = reputation;
        this.trustValue = trustValue;
    }
}
