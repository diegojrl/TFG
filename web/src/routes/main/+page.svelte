<script lang="ts">
    import DeviceLayout from "$lib/DeviceLayout.svelte";
    import EditModal from "$lib/modals/DeviceEdit.svelte"
    import {type Device} from "$lib/device";
    import {devices} from "$lib/websocket/devices.svelte";


    let editDevice: Device | undefined = $state(undefined);


</script>

{#if editDevice }
    <EditModal dev={editDevice} onClose={()=>{ editDevice=undefined;}}></EditModal>
{/if}


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

<style lang="postcss">
    @reference "tailwindcss";
    :global(html) {
        background-color: theme(--color-gray-100);
    }
</style>