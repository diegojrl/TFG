<script lang="ts">
    import { fade } from "svelte/transition";
    import DeviceLayout from "./DeviceLayout.svelte";
    import Modal from "./modals/ConnectModal.svelte";
    import { type Device } from "../lib/device";
    import {
        start_mqtt_connection,
        stop_mqtt_client,
        SERVER,
    } from "../lib/websocket";

    let devices: Device[] = $state([]);
    let server_name = $state("");
    let username = $state("");
    let password = $state("");
    let showModal = $state(false);

    function add_information(new_device: Device) {
        console.log("add info");
        let idx = devices.findIndex((d) => d.clientid == new_device.clientid);

        if (idx >= 0) {
            let dev = devices[idx];
            dev.avgDelay = new_device.avgDelay;
            dev.clientid = new_device.clientid;
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
    async function connect() {
        try {
            if (
                await start_mqtt_connection(username, password, add_information)
            ) {
                showModal = false;
                server_name = SERVER.hostname;
            } else {
                logout();
            }
        } catch (e) {
            console.log(e);
            logout();
        }
    }
    async function logout() {
        await stop_mqtt_client();
        server_name = "";
        username = "";
        password = "";
        devices = [];
        showModal = true;
    }
</script>

<Modal
    open={showModal}
    bind:username
    bind:password
    onClose={() => {
        showModal = false;
    }}
    onLogin={connect}
></Modal>

<div class="mx-auto px-2 sm:px-6 lg:px-8 bg-gray-800">
    <div class="relative flex h-16 items-center justify-between">
        {#if server_name == ""}
            <span></span>
            <button
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                onclick={() => {
                    showModal = true;
                }}>Login</button
            >
        {:else}
            <div class="bg-green-900 rounded px-3 py-1 text-white">
                {server_name}
            </div>
            <button
                class="bg-red-500 hover:bg-red-600 rounded px-3 py-1 text-white"
                onclick={logout}>Logout</button
            >
        {/if}
    </div>
</div>

{#if devices.length == 0}
    <div class="items-center justify-center fixed inset-9 flex">
        <p>No devices connected</p>
    </div>
{:else}
    <div class="grid md:grid-cols-1 lg:grid-cols-3 gap-4 m-4">
        {#each devices as dev}
            <div transition:fade class="">
                <DeviceLayout {dev} />
            </div>
        {/each}
    </div>
{/if}

<style lang="postcss">
    @reference "tailwindcss";
    :global(html) {
        background-color: theme(--color-gray-100);
    }
</style>
