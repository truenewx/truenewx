<template>
    <el-image :class="shape === 'square' ? 'rounded' : 'rounded-circle'" :style="style" :src="src"
        :preview-src-list="previewSrcList" fit="contain" v-if="preview && src">
        <div slot="error" class="text-muted h-100 flex-center">
            <i class="el-icon-picture-outline"/>
        </div>
    </el-image>
    <el-avatar :size="size" :style="style" icon="el-icon-user-solid" :shape="shape" fit="contain" :src="src" v-else/>
</template>

<script>
export default {
    name: 'TnxelAvatar',
    props: {
        url: String,
        shape: String,
        size: {
            type: Number,
            required: true,
        },
        preview: {
            type: Boolean,
            default: false,
        }
    },
    data() {
        return {
            src: null,
            previewSrcList: null,
        }
    },
    computed: {
        style() {
            if (this.preview && this.src) {
                return {
                    width: this.size + 'px',
                    height: this.size + 'px',
                    'font-size': (this.size / 2) + 'px',
                };
            } else {
                let style = {
                    'font-size': (this.size / 2) + 'px',
                };
                if (this.src) {
                    style['background-color'] = 'transparent';
                }
                return style;
            }
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
                    let fssBaseUrl = window.tnx.fss.getBaseUrl();
                    if (vm.preview) {
                        rpc.get(fssBaseUrl + '/meta', {
                            storageUrl: vm.url
                        }, function(meta) {
                            vm.src = meta.thumbnailReadUrl;
                            vm.previewSrcList = [meta.readUrl];
                        });
                    } else {
                        rpc.get(fssBaseUrl + '/read-url', {
                            storageUrl: vm.url,
                            thumbnail: true,
                        }, function(readUrl) {
                            vm.src = readUrl;
                        });
                    }
                }, {
                    app: window.tnx.fss.getAppName()
                });
            } else {
                this.src = this.url;
            }
        }
    }
}
</script>
