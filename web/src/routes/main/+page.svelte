<script lang="ts">
    import DeviceLayout from "$lib/DeviceLayout.svelte";
    import EditModal from "$lib/modals/DeviceEdit.svelte"
    import {type Device} from "$lib/device";
    import {devices} from "$lib/websocket/devices.svelte";
    import {stop_mqtt_client} from "$lib/websocket/websocket";
    import {goto} from "$app/navigation";

    async function logout() {
        await stop_mqtt_client();
        await goto("/");
    }

    let editDevice: Device | undefined = $state(undefined);


</script>

{#if editDevice }
    <EditModal dev={editDevice} onClose={()=>{ editDevice=undefined;}}></EditModal>
{/if}

<div class="px-2 sm:px-6 lg:px-8 bg-gray-800 flex h-16 items-center justify-between">
    <button
            class="bg-red-600 hover:bg-red-800 rounded-md px-3 py-1 text-white" onclick={logout}
    >Logout
    </button
    >
</div>


{#if devices.length === 0}
    <div class="items-center justify-center flex">
        <p>No devices connected</p>
    </div>
{:else}
    <div class="grid sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4 gap-4 m-4">
        {#each devices as dev}
            <DeviceLayout dev={dev} bind:editDevice={editDevice}/>
        {/each}
    </div>
{/if}
