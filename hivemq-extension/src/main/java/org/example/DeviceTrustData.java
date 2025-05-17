package org.example;

public class DeviceTrustData {
    public enum NetworkType {Internal, External};
    public enum NetworkSecurity { No, TLS };


    public Integer failedInteractionsPercentage;
    public Integer avgDelay;
    public NetworkType networkType;  //One of "Internal" | "External"
    public NetworkSecurity networkSecurity;
    public Integer reputation;

    public DeviceTrustData(Integer failedInteractionsPercentage, Integer avgDelay, NetworkType networkType, NetworkSecurity networkSecurity, Integer reputation) {
        this.failedInteractionsPercentage = failedInteractionsPercentage;
        this.avgDelay = avgDelay;
        this.networkType = networkType;
        this.networkSecurity = networkSecurity;
        this.reputation = reputation;
    }
}
