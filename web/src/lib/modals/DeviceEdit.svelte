<script lang="ts">
    import Modal from "./modal.svelte";
    import {updateOpinion} from "$lib/websocket";

    let {
        dev,
        onClose,
    } = $props();
    let opinion = $state(dev.reputation * 100);
    async function sendChanges() {
        await updateOpinion(dev.clientId, opinion / 100);
        onClose();
    }
</script>

<Modal {onClose}>
    <h2 class="text-xl font-bold mb-2 truncate">Edit {dev.clientId}</h2>
    <br/>

    <p>Opinion</p>
    <input bind:value={opinion} max="100" min="0" type="range">

    <div class="mt-4 flex justify-end">
        <button
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                onclick={sendChanges}
        >
            Ok
        </button>
    </div>
</Modal>
