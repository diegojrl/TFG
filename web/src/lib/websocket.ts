import mqtt, {type IClientOptions} from 'mqtt';
import {arrayToDevice, type Device} from './device';
import {Decoder} from "@msgpack/msgpack";
import {PUBLIC_MQTT_HOST} from '$env/static/public';

let mqttClient: mqtt.MqttClient | undefined;

const CONTROL_TOPIC = "control/view/";
const PING_TOPIC = "tmgr/ping";
const DECODER = new Decoder();
export const SERVER = new URL(PUBLIC_MQTT_HOST);

export async function start_mqtt_connection(username: string, password: string, onDevice: (dev: Device) => void, onLostConnection: () => void, onReconect: () => void): Promise<boolean> {
    try {
        await stop_mqtt_client();
        console.log("test")
        let client_options: IClientOptions = {
            username: username,
            password: password,
            protocolVersion: 5
        }
        client_options.clientId = "web_control_" + username + "_" + (Math.random() * 100).toFixed(0);
        mqttClient = await mqtt.connectAsync(SERVER.toString(), client_options);


        mqttClient.on("message", (topic, payload, packet) => {
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

        await mqttClient.subscribeAsync(CONTROL_TOPIC + "+", { qos: 1 });
        await mqttClient.subscribeAsync(PING_TOPIC, { qos: 1 });

        mqttClient.sendPing();

        mqttClient.on("connect", () => {
            console.log("Connected");
            onReconect();
        });
        mqttClient.on("reconnect", () => {
            console.log("recon");
            onReconect();
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