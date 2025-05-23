package org.example.trustData;

public class DeviceControlData {
    public enum NetworkType {Internal, External};
    public enum NetworkSecurity { No, TLS };


    public Float failedInteractionsPercentage;
    public Integer avgDelay;
    public NetworkType networkType;  //One of "Internal" | "External"
    public NetworkSecurity networkSecurity;
    public Integer reputation;

    public DeviceControlData(Float failedInteractionsPercentage, Integer avgDelay, NetworkType networkType, NetworkSecurity networkSecurity, Integer reputation) {
        this.failedInteractionsPercentage = failedInteractionsPercentage;
        this.avgDelay = avgDelay;
        this.networkType = networkType;
        this.networkSecurity = networkSecurity;
        this.reputation = reputation;
    }
}
