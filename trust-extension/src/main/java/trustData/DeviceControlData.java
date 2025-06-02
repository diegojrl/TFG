package trustData;

/**
 * @param networkType One of "Internal" | "External"
 */
public record DeviceControlData(float failedInteractionsPercentage, int avgDelay,
                                trustData.DeviceControlData.NetworkType networkType,
                                trustData.DeviceControlData.NetworkSecurity networkSecurity, float reputation,
                                float trustValue) {
    public enum NetworkType {Internal, External}

    public enum NetworkSecurity {No, TLS}


}
