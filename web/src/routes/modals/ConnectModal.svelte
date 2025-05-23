<script lang="ts">
    import Modal from "./modal.svelte";
    let {
        username = $bindable(),
        password = $bindable(),
        open,
        onClose,
        onLogin,
    } = $props();
</script>

<Modal {open} {onClose}>
    <h2 class="text-xl font-bold mb-2">Hello from the Modal</h2>

    <input
        type="text"
        class="border-gray-300 border-2 rounded-sm"
        bind:value={username}
        placeholder="Username"
    />
    <input
        type="password"
        class="border-gray-300 border-2 rounded-sm"
        bind:value={password}
        placeholder="Password"
    />

    <div class="mt-4 flex justify-end">
        <button
            class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
            onclick={onLogin}
        >
            Login
        </button>
    </div></Modal
>
