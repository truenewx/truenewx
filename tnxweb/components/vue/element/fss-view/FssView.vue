<template>
    <a :href="meta.readUrl" target="_blank" v-if="preview">{{ meta.name }}</a>
    <span v-else>{{ meta.name }}</span>
</template>

<script>
export default {
    name: 'TnxelFssView',
    props: {
        url: String,
        preview: {
            type: Boolean,
            default: false,
        }
    },
    data() {
        return {
            meta: {},
        }
    },
    watch: {
        url() {
            this.load();
        }
    },
    created() {
        this.load();
    },
    methods: {
        load() {
            if (this.url && this.url.startsWith('fss://')) {
                let rpc = window.tnx.app.rpc;
                let vm = this;
                rpc.ensureLogined(function() {
                    rpc.get(window.tnx.fss.getBaseUrl() + '/meta', {
                        storageUrl: vm.url
                    }, function(meta) {
                        vm.meta = meta;
                    });
                }, {
                    app: window.tnx.fss.getAppName()
                });
            }
        }
    }
}
</script>
