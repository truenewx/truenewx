<template>
    <div class="tnxel-fss-view" v-if="meta.readUrl">
        <span class="text-muted" v-if="denied">没有权限查看该文件</span>
        <el-image :src="meta.thumbnailReadUrl" :preview-src-list="[meta.readUrl]" fit="contain"
            :style="{width: imageWidth, height: imageHeight}" v-else-if="imageable">
            <template #error>
                <div class="text-muted h-100 flex-center">
                    <i class="el-icon-picture-outline"/>
                </div>
            </template>
        </el-image>
        <template v-else>
            <a class="overflow-ellipsis" :href="meta.readUrl" target="_blank" :title="'下载 ' + meta.name">
                {{ meta.name }}
            </a>
            <a class="preview" :href="previewUrl" target="_blank" :title="'预览 ' + meta.name" v-if="previewUrl">预览</a>
        </template>
    </div>
</template>

<script>
export default {
    name: 'TnxelFssView',
    props: {
        url: String,
        width: [String, Number],
        height: [String, Number],
    },
    data() {
        return {
            meta: {
                readUrl: null,
                thumbnailReadUrl: null,
                imageable: false,
            },
            denied: false,
        }
    },
    computed: {
        extension() {
            let extension = tnx.util.net.getExtension(this.meta.readUrl);
            if (extension) {
                return extension.toLowerCase();
            }
            return undefined;
        },
        imageable() {
            return ['jpg', 'png', 'gif', 'svg'].contains(this.extension);
        },
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
        previewUrl() {
            if (this.extension === 'pdf') {
                return tnx.util.net.appendParams(this.meta.readUrl, {
                    inline: true,
                });
            }
            return undefined;
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

<style>
.tnxel-fss-view {
    display: flex;
    align-items: center;
}

.is-center .tnxel-fss-view {
    justify-content: center;
}

.tnxel-fss-view .preview {
    margin-left: 0.75rem;
    white-space: nowrap;
}
</style>
