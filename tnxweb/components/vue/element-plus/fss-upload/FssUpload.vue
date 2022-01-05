<template>
    <tnxel-upload ref="upload" :action="action" :upload-options="uploadOptions" :file-list="fileList"
        :read-only="readOnly" :width="width" :height="height" :icon="icon" :icon-size="iconSize" :center="center"
        :hidden-tip="hiddenTip" :show-file-list="showFileList"
        :before-upload="beforeUpload" :on-success="_onSuccess" :on-error="onError" :on-removed="_onRemove"/>
</template>

<script>
import Upload from '../upload/Upload';

export default {
    components: {
        'tnxel-upload': Upload,
    },
    name: 'TnxelFssUpload',
    props: {
        modelValue: [String, Array],
        type: {
            type: String,
            required: true,
        },
        scope: [Number, String],
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
        icon: String,
        iconSize: Number,
        center: Boolean,
        hiddenTip: Boolean,
        showFileList: {
            type: Boolean,
            default() {
                return true;
            }
        },
        onSuccess: Function,
        onError: Function,
        onRemove: Function,
        beforeUpload: Function,
    },
    emits: ['update:modelValue'],
    data() {
        const tnx = window.tnx;
        return {
            tnx: tnx,
            action: tnx.fss.getUploadUrl(this.type, this.scope),
            uploadOptions: {},
            fileList: [],
        };
    },
    computed: {
        uploadFiles() {
            return this.$refs.upload ? this.$refs.upload.uploadFiles : [];
        },
    },
    watch: {
        modelValue(newValue, oldValue) {
            if (oldValue !== newValue && !this.equals(this.fileList, newValue)) {
                this._initialize();
            }
        }
    },
    mounted() {
        this._initialize();
    },
    methods: {
        equals(fileList, storageUrls) {
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
        contains(fileList, storageUrl) {
            for (let file of fileList) {
                if (file.storageUrl === storageUrl) {
                    return true;
                }
            }
            return false;
        },
        _initialize() {
            const vm = this;
            let fssConfig = vm.tnx.fss.getClientConfig();
            vm.tnx.app.rpc.ensureLogined(function() {
                let storageUrls;
                if (vm.modelValue) {
                    storageUrls = Array.isArray(vm.modelValue) ? vm.modelValue : [vm.modelValue];
                } else {
                    storageUrls = [];
                }
                if (storageUrls.length) {
                    vm.tnx.app.rpc.get(fssConfig.contextUrl + '/metas', {
                        storageUrls: storageUrls
                    }, function(metas) {
                        let fileList = [];
                        metas.forEach(meta => {
                            if (meta) {
                                fileList.push({
                                    name: meta.name,
                                    url: vm._getFullReadUrl(meta.thumbnailReadUrl || meta.readUrl),
                                    previewUrl: vm._getFullReadUrl(meta.readUrl),
                                    storageUrl: meta.storageUrl,
                                });
                            }
                        });
                        vm.fileList = fileList;
                        vm.$nextTick(function() {
                            vm._loadUploadOptions();
                        });
                    }, {
                        app: fssConfig.appName
                    });
                } else {
                    vm.$nextTick(function() {
                        vm._loadUploadOptions();
                    });
                }
            }, {
                app: fssConfig.appName,
                toLogin(loginFormUrl, originalUrl, originalMethod) {
                    return true;
                }
            });
        },
        _loadUploadOptions() {
            // 上传限制为空才执行加载，避免多次重复加载
            if (Object.keys(this.uploadOptions).length === 0) {
                let vm = this;
                vm.tnx.fss.loadUploadOptions(this.type, function(uploadOptions) {
                    vm.uploadOptions = uploadOptions;
                });
            }
        },
        _getFullReadUrl(readUrl) {
            if (readUrl && readUrl.startsWith('//')) {
                return window.location.protocol + readUrl;
            }
            return readUrl;
        },
        _onSuccess(result, file, fileList) {
            if (result) {
                file.storageUrl = result.storageUrl;
                this.fileList = fileList;
                this.emitInput();
                if (this.onSuccess) {
                    this.onSuccess(file);
                }
            }
        },
        _onRemove(file) {
            for (let i = 0; i < this.fileList.length; i++) {
                let _file = this.fileList[i];
                if (_file.id === file.id) {
                    this.fileList.splice(i, 1);
                    break;
                }
            }
            this.emitInput();
            if (this.onRemove) {
                this.onRemove(file);
            }
        },
        emitInput() {
            let storageUrls = [];
            for (let file of this.uploadFiles) {
                if (file.storageUrl) {
                    storageUrls.push(file.storageUrl);
                } else { // 存在一个未完成上传，则退出
                    return;
                }
            }
            if (this.uploadOptions.number === 1) {
                storageUrls = storageUrls[0];
            }
            this.$emit('update:modelValue', storageUrls);
        },
        size() {
            return this.$refs.upload.size();
        },
        /**
         * 校验上传是否已经全部完成
         * @param reject 没有完成上传时的处理函数，传入文件对象参数
         * @returns 文件存储路径或其数组，有上传未完成时返回false
         */
        validateUploaded(reject) {
            if (this.uploadOptions.number > 1) {
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
        _doValidateUploadedReject(reject, file) {
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
