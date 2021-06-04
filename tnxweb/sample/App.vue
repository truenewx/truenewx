<template>
    <div id="app">
        <page-index/>
    </div>
</template>

<script>
    import index from './pages/index.vue';

    export default {
        name: 'App',
        components: {
            'page-index': index,
        },
        data () {
            return {}
        }
    }
</script>
