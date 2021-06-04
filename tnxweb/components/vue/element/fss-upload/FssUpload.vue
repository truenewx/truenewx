<template>
    <tnxel-upload ref="upload" :action="action" :upload-limit="uploadLimit" :file-list="fileList" :read-only="readOnly"
        :width="width" :height="height" :on-success="onSuccess" :on-removed="emitInput"/>
</template>

<script>
import Upload from '../upload';

export default {
    components: {
        'tnxel-upload': Upload,
    },
    name: 'TnxelFssUpload',
    props: {
        value: [String, Array],
        type: {
            type: String,
            required: true,
        },
        scope: String,
        readOnly: {
            type: Boolean,
            default: () => false,
        },
        width: {
            type: [Number, String],
        },
        height: {
            type: [Number, String],
        },
    },
    data() {
        const tnx = window.tnx;
        return {
            tnx: tnx,
            action: tnx.fss.getUploadUrl(this.type, this.scope),
            uploadLimit: {},
            fileList: [],
        };
    },
    computed: {
        uploadFiles() {
            return this.$refs.upload ? this.$refs.upload.uploadFiles : [];
        },
    },
    watch: {
        value(newValue, oldValue) {
            if (!oldValue && newValue && !this.equals(this.fileList, newValue)) {
                this._init();
            }
        }
    },
    mounted() {
        this._init();
    },
    methods: {
        equals: function(fileList, storageUrls) {
            if (!Array.isArray(storageUrls)) {
                storageUrls = [storageUrls];
            }
            if (fileList.length !== storageUrls.length) {
                return false;
            }
            // 每一个存储地址都必须在文件清单中找到对应文件
            for (let storageUrl of storageUrls) {
                if (!this.contains(fileList, storageUrl)) {
                    return false;
                }
            }
            return true;
        },
        contains: function(fileList, storageUrl) {
            for (let file of fileList) {
                if (file.storageUrl === storageUrl) {
                    return true;
                }
            }
            return false;
        },
        _init: function() {
            const vm = this;
            vm.tnx.app.rpc.ensureLogined(function() {
                if (vm.value) {
                    let storageUrls = Array.isArray(vm.value) ? vm.value : [vm.value];
                    vm.tnx.app.rpc.get(vm.tnx.fss.getBaseUrl() + '/metas', {
                        storageUrls: storageUrls
                    }, function(metas) {
                        vm.fileList = [];
                        metas.forEach(meta => {
                            if (meta) {
                                vm.fileList.push({
                                    name: meta.name,
                                    url: vm._getFullReadUrl(meta.thumbnailReadUrl || meta.readUrl),
                                    previewUrl: vm._getFullReadUrl(meta.readUrl),
                                    storageUrl: meta.storageUrl,
                                });
                            }
                        });
                        vm.$nextTick(function() {
                            vm._loadUploadLimit();
                        });
                    });
                } else {
                    vm.$nextTick(function() {
                        vm._loadUploadLimit();
                    });
                }
            }, {
                app: vm.tnx.fss.getAppName(),
                toLogin: function(loginFormUrl, originalUrl, originalMethod) {
                    return true;
                }
            });
        },
        _loadUploadLimit: function() {
            // 上传限制为空才执行加载，避免多次重复加载
            if (Object.keys(this.uploadLimit).length === 0) {
                let vm = this;
                vm.tnx.fss.loadUploadLimit(this.type, function(uploadLimit) {
                    vm.uploadLimit = uploadLimit;
                });
            }
        },
        _getFullReadUrl: function(readUrl) {
            if (readUrl && readUrl.startsWith('//')) {
                return window.location.protocol + readUrl;
            }
            return readUrl;
        },
        onSuccess: function(uploadedFile, file, fileList) {
            if (uploadedFile) {
                file.storageUrl = uploadedFile.storageUrl;
                this.fileList = fileList;
                this.emitInput();
            }
        },
        emitInput: function() {
            let storageUrls = [];
            for (let file of this.uploadFiles) {
                if (file.storageUrl) {
                    storageUrls.push(file.storageUrl);
                } else { // 存在一个未完成上传，则退出
                    return;
                }
            }
            if (this.uploadLimit.number === 1) {
                storageUrls = storageUrls[0];
            }
            this.$emit('input', storageUrls);
        },
        size: function() {
            return this.$refs.upload.size();
        },
        /**
         * 校验上传是否已经全部完成
         * @param reject 没有完成上传时的处理函数，传入文件对象参数
         * @returns 文件存储路径或其数组，有上传未完成时返回false
         */
        validateUploaded: function(reject) {
            if (this.uploadLimit.number > 1) {
                const storageUrls = [];
                for (let file of this.uploadFiles) {
                    if (file.storageUrl) {
                        storageUrls.push(file.storageUrl);
                    } else {
                        this._doValidateUploadedReject(reject, file);
                        return false;
                    }
                }
                return storageUrls;
            } else if (this.uploadFiles.length) {
                let file = this.uploadFiles[0];
                if (file) {
                    if (file.storageUrl) {
                        return file.storageUrl;
                    } else {
                        this._doValidateUploadedReject(reject, file);
                        return false;
                    }
                }
            }
            return null;
        },
        _doValidateUploadedReject: function(reject, file) {
            if (typeof reject === 'function') {
                reject(file);
            } else {
                this.tnx.alert('文件"' + file.name + '"还未上传完毕，请稍候', function() {
                    if (reject && typeof reject.disable === 'function') {
                        reject.disable(false);
                    }
                });
            }
        }
    }
}
</script>
