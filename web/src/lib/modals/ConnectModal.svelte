<script lang="ts">
    import Modal from "./modal.svelte";
    import {focus} from "$lib/focus";

    let {
        username = $bindable(),
        password = $bindable(),
        onClose,
        onLogin,
    } = $props();
</script>

<Modal {onClose}>
    <div class="p-5">
        <h2 class="text-xl font-bold mb-2">Sing in</h2>
        <br>
        <form>
            <input
                    bind:value={username}
                    class="border-gray-300 border-2 py-1 rounded-md"
                    placeholder="Username"
                    type="text"
                    use:focus
            />
            <br>
            <br>
            <input
                    bind:value={password}
                    class="border-gray-300 border-2 py-1 rounded-md"
                    placeholder="Password"
                    type="password"
            />
            <br>
            <br>
            <button
                    class="mt-4 w-full bg-blue-500 text-white py-1 rounded-md hover:bg-blue-600"
                    onclick={onLogin}
            >
                Login
            </button>
        </form>
    </div>

</Modal>
