<script lang="ts">
    import Modal from "./modal.svelte";
    import {reset_reputation, update_failPct, update_latency, update_opinion} from "$lib/websocket/websocket";
    import {focus} from "$lib/focus";

    let {
        dev,
        onClose,
    } = $props();

    let opinion = $state(dev.reputation * 100);
    let ping = $state(dev.avgDelay);
    let failPct = $state(dev.failedInteractionsPercentage);

    async function send_opinion() {
        console.log(opinion / 100);
        await update_opinion(dev.clientId, opinion / 100);
    }

    async function send_ping() {
        console.log("Ping: " + ping);
        await update_latency(dev.clientId, ping);
    }

    async function send_failPct() {
        console.log("failPct: " + failPct);
        await update_failPct(dev.clientId, failPct);
    }

    async function send_reset_reputation() {
        await reset_reputation(dev.clientId);
    }
</script>

<Modal {onClose}>
    <h2 class="text-xl font-bold mb-2 truncate">Editar {dev.clientId}</h2>
    <br/>
    <table class="table-auto">
        <tbody>
        <tr class="mb-2">
            <td>Ping</td>
            <td><input bind:value={ping} class="mr-2 ml-2" max="10000" min="0" type="number" use:focus></td>
            <td>
                <button class="bg-blue-500 text-white px-1 py-0 rounded hover:bg-blue-600" onclick={send_ping}>
                    Mod
                </button>
            </td>
        </tr>
        <tr>
            <td>Tasa de errores</td>
            <td><input bind:value={failPct} class="mr-2 ml-2" max="100" min="0" type="range"></td>
            <td>
                <button class="bg-blue-500 text-white px-1 py-0 rounded hover:bg-blue-600" onclick={send_failPct}>
                    Mod
                </button>
            </td>
        </tr>
        <tr>
            <td>Reputación</td>
            <td></td>
            <td>
                <button class="bg-blue-500 text-white px-1 py-0 rounded hover:bg-blue-600"
                        onclick={send_reset_reputation}>
                    Resetear
                </button>
            </td>
        </tr>
        <tr>
            <td>Opinion</td>
            <td><input bind:value={opinion} class="mr-2 ml-2" max="100" min="0" type="range"></td>
            <td>
                <button class="bg-blue-500 text-white px-1 py-0 rounded hover:bg-blue-600" onclick={send_opinion}>
                    Opinar
                </button>
            </td>
        </tr>
        </tbody>
    </table>


    <div class="mt-4 flex justify-end">
        <button
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                onclick={onClose}
        >
            Ok
        </button>
    </div>
</Modal>


<style lang="postcss">
    table tr {
        td:nth-child(1) {
            text-align: start;
        }

        td:nth-child(2) {
            text-align: center;
        }

        td:nth-child(3) {
            text-align: end;
        }
    }
</style>