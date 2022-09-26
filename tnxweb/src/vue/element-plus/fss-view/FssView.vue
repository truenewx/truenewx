<template>
    <div class="tnxel-fss-view" v-if="meta.readUrl">
        <span class="text-muted" v-if="denied">没有权限查看该文件</span>
        <template v-else-if="thumbnailIconValue">
            <tnxel-button type="primary" link :icon="thumbnailIconValue" :title="$attrs.title || meta.name"
                @click="toPreview"/>
            <el-image-viewer :url-list="[previewUrl]" teleported @close="imagePreviewing = false"
                v-if="imagePreviewing"/>
        </template>
        <el-image :src="meta.thumbnailReadUrl" :preview-src-list="[meta.readUrl]" fit="contain" preview-teleported
            :style="{width: imageWidth, height: imageHeight}" v-else-if="imageable">
            <template #error>
                <div class="text-muted h-100 flex-center">
                    <i class="el-icon-picture-outline"/>
                </div>
            </template>
        </el-image>
        <template v-else>
            <a class="overflow-ellipsis" :href="downloadUrl" target="_blank" :title="'下载 ' + meta.name">
                {{ meta.name }}
            </a>
            <a class="preview" :href="previewUrl" target="_blank" :title="'预览 ' + meta.name"
                v-if="previewUrl">预览</a>
        </template>
    </div>
</template>

<script>
import Button from '../button/Button';

const tnx = window.tnx;

export default {
    name: 'TnxelFssView',
    components: {
        'tnxel-button': Button,
    },
    props: {
        url: String,
        width: [String, Number],
        height: [String, Number],
        thumbnailIcon: { // 为Boolean时表示是否仅显示缩略图标，图标采用与扩展名匹配的图标，为String时仅显示指定图标
            type: [Boolean, String],
            default: false,
        },
    },
    data() {
        return {
            meta: {
                readUrl: null,
                thumbnailReadUrl: null,
                imageable: false,
            },
            denied: false,
            imagePreviewing: false,
        }
    },
    computed: {
        extension() {
            return tnx.util.net.getExtension(this.meta.readUrl);
        },
        imageable() {
            return tnx.util.file.isImage(this.extension);
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
        downloadUrl() {
            return process.env.VUE_APP_API_BASE_URL + this.meta.downloadUrl;
        },
        previewUrl() {
            if (this.imageable) {
                return this.meta.readUrl; // 图片预览尽量使用第三方文件服务提供商地址
            }
            if (this.extension === 'pdf') { // pdf预览会打开新页面，故使用自有地址，尽量不暴露第三方文件服务提供商地址
                return tnx.util.net.appendParams(this.downloadUrl, {
                    inline: true,
                });
            }
            return undefined;
        },
        thumbnailIconValue() {
            if (this.thumbnailIcon) {
                if (typeof this.thumbnailIcon === 'string') {
                    return this.thumbnailIcon;
                }
                if (this.imageable) {
                    return 'bi bi-file-earmark-image';
                }
                if (this.extension === 'pdf') {
                    return 'bi bi-file-earmark-pdf';
                }
                return 'bi bi-file-earmark';
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
                    locationUrl: vm.url,
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
        },
        toPreview() {
            if (this.imageable) {
                this.imagePreviewing = true;
            } else {
                tnx.util.bom.openUniquely(this.previewUrl);
            }
        },
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
