<template>
    <el-image :class="shape === 'square' ? 'rounded' : 'rounded-circle'" :style="style" :src="src"
        :preview-src-list="previewSrcList" v-if="preview && src">
        <template #error>
            <div class="text-muted h-100 flex-center">
                <i class="el-icon-picture-outline"/>
            </div>
        </template>
    </el-image>
    <el-avatar :size="size" :style="style" :shape="shape" :src="src" v-else>
        <tnxel-icon :type="icon" :size="size/2" style="margin:0" v-if="!src && icon"/>
    </el-avatar>
</template>

<script>
import Icon from '../icon/Icon';

export default {
    name: 'TnxelAvatar',
    components: {
        'tnxel-icon': Icon,
    },
    props: {
        url: String,
        shape: String,
        size: {
            type: Number,
            required: true,
        },
        icon: {
            type: String,
            default: 'el-icon-user-solid',
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
