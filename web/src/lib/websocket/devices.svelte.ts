import type {Device} from "$lib/device";

export const devices: Device[] = $state([]);


export function add_information(new_device: Device) {
    console.log("add info");
    let idx = devices.findIndex((d) => d.clientId == new_device.clientId);

    if (idx >= 0) {
        let dev = devices[idx];
        dev.avgDelay = new_device.avgDelay;
        dev.clientId = new_device.clientId;
        dev.failedInteractionsPercentage =
            new_device.failedInteractionsPercentage;
        dev.networkSecurity = new_device.networkSecurity;
        dev.networkType = new_device.networkType;
        dev.reputation = new_device.reputation;
        dev.trust = new_device.trust;
    } else {
        devices.push(new_device);
    }
}



