
export enum NetworkType { Internal, External };
export enum NetworkSecurity { No, TLS };

export type Device = {
  clientid: String;
  failedInteractionsPercentage: number;
  avgDelay: number;
  networkType: NetworkType;
  networkSecurity: NetworkSecurity;
  reputation: number;
  trust: number;
};

export function arrayToDevice(data: unknown, clientId: String): Device {
  if (!Array.isArray(data) || data.length !== 6) {
    throw new Error("Invalid data format for Device");
  }

  const [
    failedInteractionsPercentage,
    avgDelay,
    networkType,
    networkSecurity,
    reputation,
    trust,
  ] = data;

  return {
    clientid: clientId,
    failedInteractionsPercentage: Number(failedInteractionsPercentage * 100),
    avgDelay: Number(avgDelay),
    networkType: NetworkType[Number(networkType)] as unknown as NetworkType,
    networkSecurity: NetworkSecurity[Number(networkSecurity)] as unknown as NetworkSecurity,
    reputation: Number(reputation),
    trust: Number(trust)
  };
}
