<script lang="ts">
    import LoginModal from "$lib/modals/ConnectModal.svelte";
    import {connect} from "$lib/websocket/connection";

    let username = $state("");
    let password = $state("");

    async function login() {
        await connect(username, password);
        password = ""
    }
</script>

<LoginModal
        bind:password
        bind:username
        onClose={() => {}}
        onLogin={login}>
</LoginModal>


<style lang="postcss">
    @reference "tailwindcss";
    :global(html) {
        background-color: theme(--color-gray-500);
    }
</style>
