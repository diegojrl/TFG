<script lang="ts">
    import Modal from "./modal.svelte";

    let {
        username = $bindable(),
        password = $bindable(),
        onClose,
        onLogin,
    } = $props();
</script>

<Modal {onClose}>
    <h2 class="text-xl font-bold mb-2">Hello from the Modal</h2>
    <br/>
    <input
            bind:value={username}
            class="border-gray-300 border-2 rounded-sm"
            placeholder="Username"
            type="text"
    />
    <br/>
    <br/>
    <input
            bind:value={password}
            class="border-gray-300 border-2 rounded-sm"
            placeholder="Password"
            type="password"
    />

    <div class="mt-4 flex justify-end">
        <button
                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
                onclick={onLogin}
        >
            Login
        </button>
    </div>
</Modal>
