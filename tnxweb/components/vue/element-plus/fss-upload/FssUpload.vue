<template>
    <tnxel-upload ref="upload"
        :app-name="appName"
        :action="action"
        :upload-options="uploadOptions"
        :file-list="fileList"
        :width="width" :height="height"
        :icon="icon" :icon-size="iconSize"
        :center="center"
        :trigger-text="triggerText"
        :tip="tip"
        :show-file-list="showFileList"
        :data="params"
        :before-upload="beforeUpload"
        :on-upload="onUpload"
        :on-progress="onProgress"
        :on-success="_onSuccess"
        :on-error="onError"
        :on-removed="_onRemove"
        :handle-errors="handleErrors"/>
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
        width: {
            type: [Number, String],
        },
        height: {
            type: [Number, String],
        },
        icon: String,
        iconSize: Number,
        triggerText: String,
        center: Boolean,
        tip: {
            type: [Boolean, String, Function],
            default() {
                return true;
            }
        },
        showFileList: {
            type: Boolean,
            default() {
                return true;
            }
        },
        onlyStorage: Boolean, // 上传完成后，是否只需要返回存储地址，以减少数据传输量
        extension: [String, Array], // 可接受的文件扩展名集合，最终实际生效的扩展名集合，是它与服务端上传配置中的扩展名限制集合的并集
        beforeUpload: Function,
        onUpload: Function,
        onProgress: Function,
        onSuccess: Function,
        onError: Function,
        onRemove: Function,
        handleErrors: Function,
    },
    emits: ['update:modelValue'],
    data() {
        const tnx = window.tnx;
        return {
            tnx: tnx,
            appName: tnx.fss.appName,
            action: tnx.fss.getUploadUrl(this.type, this.scope),
            params: {
                onlyStorage: this.onlyStorage,
            },
            uploadOptions: {},
            fileList: [],
        };
    },
    computed: {
        uploadingFiles() {
            return this.$refs.upload?.uploadingFiles || [];
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
        equals(fileList, locationUrls) {
            if (!locationUrls) {
                return !fileList.length;
            }
            if (!Array.isArray(locationUrls)) {
                locationUrls = [locationUrls];
            }
            if (fileList.length !== locationUrls.length) {
                return false;
            }
            // 每一个存储地址都必须在文件清单中找到对应文件
            for (let locationUrl of locationUrls) {
                if (!this.contains(fileList, locationUrl)) {
                    return false;
                }
            }
            return true;
        },
        contains(fileList, locationUrl) {
            for (let file of fileList) {
                if (file.locationUrl === locationUrl) {
                    return true;
                }
            }
            return false;
        },
        _initialize() {
            const vm = this;
            let fssConfig = vm.tnx.fss.getClientConfig();
            vm.tnx.app.rpc.ensureLogined(function() {
                let locationUrls;
                if (vm.modelValue) {
                    locationUrls = Array.isArray(vm.modelValue) ? vm.modelValue : [vm.modelValue];
                } else {
                    locationUrls = [];
                }
                if (locationUrls.length) {
                    vm.tnx.app.rpc.get(fssConfig.contextUrl + '/metas', {
                        locationUrls: locationUrls
                    }, function(metas) {
                        let fileList = [];
                        metas.forEach(meta => {
                            if (meta) {
                                fileList.push({
                                    name: meta.name,
                                    url: vm._getFullReadUrl(meta.thumbnailReadUrl || meta.readUrl),
                                    previewUrl: vm._getFullReadUrl(meta.readUrl),
                                    locationUrl: meta.locationUrl,
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
                    vm.fileList = [];
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
                    if (vm.extension) {
                        let extensions = Array.isArray(vm.extension) ? vm.extension : [vm.extension];
                        let acceptedExtensions = [];
                        if (uploadOptions.extensionsRejected) { // 服务端上传限制扩展名为排除模式
                            for (let extension of extensions) {
                                if (!uploadOptions.extensions.contains(extension)) { // 取未被排除的
                                    acceptedExtensions.push(extension);
                                }
                            }
                        } else { // 服务端上传限制扩展名为包含模式
                            if (uploadOptions.extensions.length) {
                                for (let extension of extensions) {
                                    if (uploadOptions.extensions.contains(extension)) { // 取被包含的
                                        acceptedExtensions.push(extension);
                                    }
                                }
                            } else { // 未限制扩展名，则直接加入
                                for (let extension of extensions) {
                                    acceptedExtensions.push(extension);
                                }
                            }
                        }
                        if (acceptedExtensions.length) { // 接受的扩展名清单不为空，则替代服务端配置中的扩展名限制
                            uploadOptions.extensionsRejected = false;
                            uploadOptions.extensions = acceptedExtensions;
                        }
                    }
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
                file.locationUrl = result.locationUrl;
                file.readUrl = result.readUrl;
                file.downloadUrl = result.downloadUrl;
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
            let locationUrls = [];
            for (let file of this.fileList) {
                if (file.locationUrl) {
                    locationUrls.push(file.locationUrl);
                } else { // 存在一个未完成上传，则退出
                    return;
                }
            }
            if (this.uploadOptions.number === 1) {
                locationUrls = locationUrls[0];
            }
            this.$emit('update:modelValue', locationUrls);
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
                const locationUrls = [];
                for (let file of this.uploadingFiles) {
                    if (file.locationUrl) {
                        locationUrls.push(file.locationUrl);
                    } else {
                        this._doValidateUploadedReject(reject, file);
                        return false;
                    }
                }
                return locationUrls;
            } else if (this.uploadingFiles.length) {
                let file = this.uploadingFiles[0];
                if (file) {
                    if (file.locationUrl) {
                        return file.locationUrl;
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
        },
    }
}
</script>
