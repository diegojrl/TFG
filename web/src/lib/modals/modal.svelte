<script>
    import {fade, slide} from "svelte/transition";

    let {onClose, children} = $props();
</script>


<div
        class="modal fixed w-full h-full items-center justify-center flex bg-gray-900/50"
        onclick={onClose}
        onkeyup={() => {}}
        role="dialog"
        tabindex="-1"
        transition:fade
>
    <div
            class="bg-white lg:h-max w-max mx-auto rounded-lg shadow-x1 z-50 overflow-y-auto p-5"
            onclick={(e) => e.stopPropagation()}
            onkeyup={(e) => {
        if (e.key === "Escape") {
          onClose();
        }
      }}
            role="dialog"
            tabindex="0"
            transition:slide
    >
        {@render children()}
    </div>
</div>

