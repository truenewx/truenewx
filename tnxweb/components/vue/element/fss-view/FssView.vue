<template>
    <el-image :src="meta.thumbnailReadUrl" :preview-src-list="[meta.readUrl]" fit="contain" v-if="meta.imageable">
        <div slot="error" class="text-muted h-100 flex-center">
            <i class="el-icon-picture-outline"/>
        </div>
    </el-image>
    <a :href="meta.readUrl" target="_blank" v-else-if="preview">{{ meta.name }}</a>
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
            if (this.url && !this.url.startsWith('http://') && !this.url.startsWith('https://')) {
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
