import mqtt, { type IClientOptions } from 'mqtt';
import { arrayToDevice, type Device } from './device';
import { Decoder } from "@msgpack/msgpack";
import { PUBLIC_MQTT_HOST } from '$env/static/public';

export let mqttClient: mqtt.MqttClient | undefined;

const CONTROL_TOPIC = "control/view/";
const PING_TOPIC = "tmgr/ping";
const DECODER = new Decoder();
export const SERVER = new URL(PUBLIC_MQTT_HOST);

export async function start_mqtt_connection(username: string, password: string, fn: (dev: Device) => void): Promise<boolean> {
    try {
        if (mqttClient != undefined) {
            await mqttClient.endAsync();
        }
        console.log("test")
        let client_options: IClientOptions = {
            username: username,
            password: password,

        };
        client_options.clientId = "web_control_" + Math.random() * 10;
        mqttClient = await mqtt.connectAsync(SERVER.toString(), client_options);

        await mqttClient.subscribeAsync(CONTROL_TOPIC + "+", { qos: 1 });
        await mqttClient.subscribeAsync(PING_TOPIC, { qos: 1 });

        mqttClient.sendPing();


        mqttClient.on("message", (topic, payload, packet) => {
            console.log("new msg");
            let topic_idx = topic.indexOf(CONTROL_TOPIC);
            if (topic_idx != -1) {
                try {
                    console.log(payload);
                    let clientid = topic.substring(topic_idx + CONTROL_TOPIC.length);
                    let device: Device = arrayToDevice(DECODER.decode(payload), clientid);
                    console.log(device)
                    fn(device);
                } catch (e) {
                    console.error(e);
                }

            }
        });

        mqttClient.on("connect", () => {

        });
        mqttClient.on("reconnect", () => {
            
        });

        return true;
    } catch (e) {
        console.log("Cant connect");
        console.log(e);
        return false;
    }

}
