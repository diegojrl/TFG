<script>
  import { fade } from "svelte/transition";
  let {
    open,
    server = $bindable(),
    username = $bindable(),
    password = $bindable(),
    onClose,
  } = $props();
</script>

{#if open}
  <div
    class="fixed inset-0 flex items-center justify-center bg-black/25 z-50"
    transition:fade={{ duration: 100 }}
  >
    <div class="bg-white rounded-2xl shadow-xl max-w-md w-full p-6 relative">
      <h2 class="text-xl font-bold mb-2">Hello from the Modal</h2>
      
      <input
        type="text"
        class="border-gray-300 border-2 rounded-sm"
        bind:value={server}
        placeholder="wss://example.com/mqtt"
      />
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
          onclick={onClose}
        >
          Login
        </button>
      </div>
    </div>
  </div>
{/if}
