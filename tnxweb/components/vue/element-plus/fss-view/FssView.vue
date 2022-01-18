<template>
    <span class="text-muted" v-if="denied">没有权限查看该图片</span>
    <el-image :src="meta.thumbnailReadUrl" :preview-src-list="[meta.readUrl]" fit="contain"
        :style="{width: imageWidth, height: imageHeight}" v-else-if="meta.imageable">
        <template #error>
            <div class="text-muted h-100 flex-center">
                <i class="el-icon-picture-outline"/>
            </div>
        </template>
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
        },
        width: [String, Number],
        height: [String, Number],
    },
    data() {
        return {
            meta: {},
            denied: false,
        }
    },
    computed: {
        imageWidth() {
            let size = this.meta.size;
            let width = this.width || (size ? size.width : undefined);
            if (typeof width === 'number') {
                width += 'px';
            }
            return width;
        },
        imageHeight() {
            let size = this.meta.size;
            let height = this.height || (size ? size.height : undefined);
            if (typeof height === 'number') {
                height += 'px';
            }
            return height;
        },
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
            if (this.url && this.url.startsWith(window.tnx.fss.PROTOCOL)) {
                let rpc = window.tnx.app.rpc;
                let fssConfig = window.tnx.fss.getClientConfig();
                let vm = this;
                rpc.get(fssConfig.contextUrl + '/meta', {
                    storageUrl: vm.url,
                }, function(meta) {
                    vm.meta = meta;
                }, {
                    app: fssConfig.appName,
                    error(errors) {
                        vm.denied = true;
                        console.error(errors[0].message);
                    }
                });
            }
        }
    }
}
</script>
