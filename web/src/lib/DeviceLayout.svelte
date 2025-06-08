<script lang="ts">
    import type {Device} from "$lib/device";
    import {fade} from "svelte/transition";

    let {dev, editDevice = $bindable()}: { dev: Device, editDevice: Device | undefined } = $props();

    function edit() {
        editDevice = dev;
        console.log("Edit")
    }
</script>

<div class="overflow-hidden bg-blue-200 rounded-md p-4"
     transition:fade>
    <div class="flex justify-between">
        <b class="truncate">{dev.clientId}</b>
        <button
                aria-label="s"
                class="hover:text-fuchsia-600 text-purple-800"
                onclick={edit}
        >
            <svg
                    class="size-6"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
            >
                <path
                        d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-8.4 8.4a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32l8.4-8.4Z"
                />
                <path
                        d="M5.25 5.25a3 3 0 0 0-3 3v10.5a3 3 0 0 0 3 3h10.5a3 3 0 0 0 3-3V13.5a.75.75 0 0 0-1.5 0v5.25a1.5 1.5 0 0 1-1.5 1.5H5.25a1.5 1.5 0 0 1-1.5-1.5V8.25a1.5 1.5 0 0 1 1.5-1.5h5.25a.75.75 0 0 0 0-1.5H5.25Z"
                />
            </svg>
        </button>
    </div>


    <br/>
    <p><b>Latencia:</b> {dev.avgDelay}</p>
    <p><b>% de errores:</b> {dev.failedInteractionsPercentage.toFixed(2)}</p>
    <p><b>Tipo de red:</b> {dev.networkType}</p>
    <p><b>Cifrado:</b> {dev.networkSecurity}</p>
    <p><b>Reputación:</b> {dev.reputation}</p>
    <div class="text-end flex items-center">
        <div class="w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700 ">
            <div
                    class="bg-blue-600 h-2.5 rounded-full"
                    style="width: {dev.trust * 100}%"
            ></div>

        </div>
        <div class="pl-2">{(dev.trust * 100).toFixed(2)}%</div>
    </div>
</div>
