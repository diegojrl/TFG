<script>
  import { fade } from "svelte/transition";
  let { open, onClose, children } = $props();
</script>

{#if open}
  <div
    class="modal fixed w-full h-full top-0 left-0 items-center justify-center z-50 flex"
    onclick={onClose}
    onkeyup={() => {}}
    role="dialog"
    tabindex="-1"
  >
    <div
      class="modal-overlay fixed w-full h-full bg-gray-900/50"
      transition:fade={{ duration: 100 }}
    ></div>
    <div
      class="bg-white w-full lg:h-max w-max mx-auto rounded-lg shadow-x1 z-50 overflow-y-auto p-5"
      onclick={(e) => e.stopPropagation()}
      onkeyup={(e) => {
        if (e.key == "Escape") {
          onClose();
        }
      }}
      tabindex="0"
      role="dialog"
    >
      {@render children()}
    </div>
  </div>
{/if}
