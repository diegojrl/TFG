<script>
    import {stop_mqtt_client} from "$lib/websocket/websocket.js";
    import {goto} from "$app/navigation";

    let {children} = $props();

    async function logout() {
        await stop_mqtt_client();
        await goto("/");
    }
</script>

<div class="mx-auto px-2 sm:px-6 lg:px-8 bg-gray-800 relative flex h-16 items-center justify-between">
    <button
            class="bg-red-500 hover:bg-red-600 rounded px-3 py-1 text-white" onclick={logout}
    >Logout
    </button
    >
</div>

{@render children()}
