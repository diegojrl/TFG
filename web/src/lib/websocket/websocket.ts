import mqtt, {type IClientOptions} from 'mqtt';
import {arrayToDevice, type Device} from '$lib/device';
import {Decoder} from "@msgpack/msgpack";
import {PUBLIC_MQTT_HOST} from '$env/static/public';

let mqttClient: mqtt.MqttClient | undefined;

const CONTROL_TOPIC: string = "control/view/";
const DECODER: Decoder = new Decoder();
const SERVER: URL = new URL(PUBLIC_MQTT_HOST);

export async function start_mqtt_connection(username: string, password: string, onDevice: (dev: Device) => void, onLostConnection: () => void, onReconect: () => void): Promise<boolean> {
    try {
        await stop_mqtt_client();
        console.log("test")
        let client_options: IClientOptions = {
            username: username,
            password: password,
            protocolVersion: 5
        }
        client_options.clientId = "web control " + username; //+ "_" + (Math.random() * 100).toFixed(0);
        mqttClient = await mqtt.connectAsync(SERVER.toString(), client_options);


        mqttClient.on("message", (topic, payload) => {
            console.log("new msg");
            let topic_idx = topic.indexOf(CONTROL_TOPIC);
            if (topic_idx != -1) {
                try {
                    console.log(payload);
                    let clientid = topic.substring(topic_idx + CONTROL_TOPIC.length);
                    let device: Device = arrayToDevice(DECODER.decode(payload), clientid);
                    onDevice(device);
                } catch (e) {
                    console.error(e);
                }

            }
        });

        mqttClient.sendPing();

        await mqttClient.subscribeAsync(CONTROL_TOPIC + "+", {qos: 1});


        mqttClient.on("connect", () => {
            console.log("Connected");
            onReconect();
        });
        mqttClient.on("reconnect", () => {
            console.log("recon");
            onLostConnection();
        });

        return true;
    } catch (e) {
        console.log("Cant connect");
        console.log(e);
        return false;
    }

}

export async function stop_mqtt_client() {
    if (mqttClient != undefined) {
        await mqttClient.endAsync();
        mqttClient = undefined;
    }
}

export async function update_opinion(clientId: string, opinion: number) {
    if (opinion < 0 || opinion > 1) {
        throw new Error("opinion out of range, " + opinion);
    }
    if (mqttClient != undefined) {
        const buf: ArrayBuffer = new ArrayBuffer(4);
        const v: DataView = new DataView(buf);
        v.setFloat32(0, opinion)
        // @ts-ignore
        await mqttClient.publishAsync("tmgr/rep/" + clientId, new Uint8Array(buf));
    } else {
        throw new Error("Not connected to mqtt server");
    }
}

export async function reset_reputation(clientId: string) {
    if (mqttClient != undefined) {
        await mqttClient.publishAsync("control/mod/" + clientId + "/rep", "");
    }
}

export async function update_failPct(clientId: string, value: number) {
    if (0 > value || value > 100) {
        throw new Error("failPct value out of range, " + value);
    }

    if (mqttClient != undefined) {
        const buf: ArrayBuffer = new ArrayBuffer(4);
        const v: DataView = new DataView(buf);
        v.setInt32(0, value)
        // @ts-ignore
        await mqttClient.publishAsync("control/mod/" + clientId + "/failPctr", new Uint8Array(buf));
    } else {
        throw new Error("Not connected to mqtt server");
    }

}

export async function update_latency(clientId: string, value: number) {
    if (mqttClient != undefined) {
        const buf: ArrayBuffer = new ArrayBuffer(4);
        const v: DataView = new DataView(buf);
        v.setInt32(0, value)
        // @ts-ignore
        await mqttClient.publishAsync("control/mod/" + clientId + "/ping", new Uint8Array(buf));
    } else {
        throw new Error("Not connected to mqtt server");
    }
}