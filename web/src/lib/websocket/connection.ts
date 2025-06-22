import {start_mqtt_connection, stop_mqtt_client} from "$lib/websocket/websocket";
import {add_information, devices} from "$lib/websocket/devices.svelte"
import {goto} from "$app/navigation";

export async function connect(username: string, password: string) {
    try {
        if (
            await start_mqtt_connection(
                username,
                password,
                add_information,
                onLostConnection,
                onReconect,
            )
        ) {
            await goto("/main");
        } else {
            await close_connection();
        }
    } catch (e) {
        console.log(e);
        await close_connection();
    }
}

export async function close_connection() {
    await stop_mqtt_client();
    devices.length = 0;
}

function onReconect() {

}

function onLostConnection() {
    devices.length = 0;
}