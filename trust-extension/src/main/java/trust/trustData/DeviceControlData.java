package trust.trustData;

/**
 * @param networkType One of "Internal" | "External"
 */
public record DeviceControlData(float failedInteractionsPercentage, int avgDelay,
                                trust.trustData.DeviceControlData.NetworkType networkType,
                                trust.trustData.DeviceControlData.NetworkSecurity networkSecurity, float reputation,
                                float trustValue) {
    public enum NetworkType {Internal, External}

    public enum NetworkSecurity {No, TLS}


}
