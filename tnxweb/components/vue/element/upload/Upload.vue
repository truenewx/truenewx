<template>
    <el-upload ref="upload" class="d-none"
        :id="id"
        name="file"
        :action="action"
        :before-upload="beforeUpload"
        :on-progress="onProgress"
        :on-success="_onSuccess"
        :on-error="onError"
        :with-credentials="true"
        list-type="picture-card"
        :file-list="fileList"
        :headers="uploadHeaders"
        :multiple="uploadLimit ? uploadLimit.number > 1 : false"
        :accept="uploadAccept">
        <i slot="default" class="el-icon-plus"></i>
        <div slot="file" slot-scope="{file}" class="el-upload-list__panel" :data-file-id="getFileId(file)">
            <img class="el-upload-list__item-thumbnail" :src="file.url" v-if="uploadLimit && uploadLimit.imageable">
            <div v-else>
                <i class="el-icon-document"></i> {{ file.name }}
            </div>
            <label class="el-upload-list__item-status-label">
                <i class="el-icon-upload-success el-icon-check"></i>
            </label>
            <span class="el-upload-list__item-uploading" v-if="file.uploading">
                <i class="el-icon-loading"></i>
            </span>
            <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="previewFile(file)"
                    v-if="uploadLimit && uploadLimit.imageable">
                    <i class="el-icon-zoom-in"></i>
                </span>
                <span class="el-upload-list__item-delete" @click="removeFile(file)" v-if="!readOnly">
                    <i class="el-icon-delete"></i>
                </span>
            </span>
        </div>
        <div slot="tip" class="el-upload__tip" v-if="tip" v-text="tip"></div>
    </el-upload>
</template>

<script>
import $ from 'jquery';

export default {
    name: 'TnxelUpload',
    props: {
        appName: String, // 上传目标应用名称
        action: {
            type: String,
            required: true,
        },
        uploadLimit: Object,
        fileList: {
            type: Array,
            default: () => [],
        },
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
        onSuccess: Function,
        onRemoved: Function,
        handleErrors: {
            type: Function,
            default: function(errors) {
                window.tnx.app.rpc.handleErrors(errors);
            }
        }
    },
    data() {
        const tnx = window.tnx;
        return {
            tnx: tnx,
            id: 'upload-container-' + tnx.util.string.random(32),
            tipMessages: {
                number: '最多只能上传{0}个文件',
                capacity: '单个文件不能超过{0}',
                extensions: '只能上传{0}文件',
                excludedExtensions: '不能上传{0}文件',
            },
            uploadHeaders: {
                'X-Requested-With': 'XMLHttpRequest'
            },
        };
    },
    computed: {
        tip() {
            if (!this.readOnly && this.uploadLimit) {
                let tip = '';
                const separator = '，';
                if (this.uploadLimit.number > 1) {
                    tip += separator + this.tipMessages.number.format(this.uploadLimit.number);
                }
                if (this.uploadLimit.capacity > 0) {
                    const capacity = this.tnx.util.string.getCapacityCaption(this.uploadLimit.capacity, 2);
                    tip += separator + this.tipMessages.capacity.format(capacity);
                }
                if (this.uploadLimit.extensions && this.uploadLimit.extensions.length) {
                    const extensions = this.uploadLimit.extensions.join('、');
                    if (this.uploadLimit.extensionsRejected) {
                        tip += separator + this.tipMessages.excludedExtensions.format(extensions);
                    } else {
                        tip += separator + this.tipMessages.extensions.format(extensions);
                    }
                }
                if (tip.length > 0) {
                    tip = tip.substr(separator.length);
                }
                return tip;
            }
            return undefined;
        },
        uploadAccept() {
            if (this.uploadLimit && this.uploadLimit.mimeTypes) {
                return this.uploadLimit.mimeTypes.join(',');
            }
            return undefined;
        },
        uploadFiles() {
            return this.$refs.upload ? this.$refs.upload.uploadFiles : [];
        },
    },
    watch: {
        uploadLimit() {
            this.render();
        }
    },
    methods: {
        render() {
            const $container = $('#' + this.id);
            // 初始化显示尺寸
            let width = this.width;
            let height = this.height;
            let uploadSize = undefined;
            if (this.uploadLimit.sizes && this.uploadLimit.sizes.length) {
                uploadSize = this.uploadLimit.sizes[0];
            }
            if (uploadSize) {
                width = width || uploadSize.width;
                height = height || uploadSize.height;
            }
            width = width || 128;
            height = height || (this.uploadLimit.imageable ? 128 : 40);
            let plusSize = Math.floor(Math.min(width, height) / 3);
            plusSize = Math.max(16, Math.min(plusSize, 32));

            width = window.tnx.util.string.getPixelString(width);
            height = window.tnx.util.string.getPixelString(height);
            const $upload = $('.el-upload', $container);
            $upload.css({
                width: width,
                height: height,
            });

            $('.el-icon-plus', $upload).css({
                fontSize: plusSize + 'px'
            });
            $container.removeClass('d-none');

            if (this.fileList && this.fileList.length) {
                for (let file of this.fileList) {
                    this._resizeFilePanel(file, this.fileList);
                }
            }
        },
        getFileId: function(file) {
            if (!file.id) {
                if (file.url) { // 有URL的文件通过URL即可唯一确定
                    file.id = this.tnx.util.md5(file.url);
                } else {
                    // 没有URL的文件，通过文件类型+文件名+文件大小+最后修改时间，几乎可以唯一区分一个文件，重复的概率极低，即使重复也不破坏业务一致性和完整性
                    file.id = this.tnx.util.md5(
                        file.type + '-' + file.name + '-' + file.size + '-' + file.lastModified);
                }
            }
            return file.id;
        },
        validate: function(file) {
            // 校验文件重复
            const vm = this;
            if (this.uploadFiles.contains(function(f) {
                const raw = f.raw ? f.raw : f;
                return file.uid !== raw.uid && vm.getFileId(file) === vm.getFileId(raw);
            })) {
                return false;
            }
            // 校验数量
            if (this.uploadLimit.number > 0 && this.uploadFiles.length > this.uploadLimit.number) {
                let message = this.tipMessages.number.format(this.uploadLimit.number);
                message += '，多余的文件未加入上传队列';
                this.tnx.error(message);
                return false;
            }
            // 校验容量大小
            if (this.uploadLimit.capacity > 0 && file.size > this.uploadLimit.capacity) {
                const capacity = this.tnx.util.string.getCapacityCaption(this.uploadLimit.capacity);
                let message = this.tipMessages.capacity.format(capacity, 2);
                message += '，文件"' + file.name + '"大小为' + this.tnx.util.string.getCapacityCaption(file.size,
                    2) + '，不符合要求';
                this.tnx.error(message);
                return false;
            }
            // 校验扩展名
            if (this.uploadLimit.extensions && this.uploadLimit.extensions.length) {
                const extension = file.name.substr(file.name.lastIndexOf('.') + 1);
                if (this.uploadLimit.extensionsRejected) { // 扩展名黑名单模式
                    if (this.uploadLimit.extensions.containsIgnoreCase(extension)) {
                        const extensions = this.uploadLimit.extensions.join('、');
                        this.tnx.error(this.tipMessages.excludedExtensions.format(extensions));
                        return false;
                    }
                } else { // 扩展名白名单模式
                    if (!this.uploadLimit.extensions.containsIgnoreCase(extension)) {
                        const extensions = this.uploadLimit.extensions.join('、');
                        let message = this.tipMessages.extensions.format(extensions);
                        message += '，文件"' + file.name + '"不符合要求';
                        this.tnx.error(message);
                        return false;
                    }
                }
            }
            return true;
        },
        beforeUpload: function(file) {
            const vm = this;
            const rpc = this.tnx.app.rpc;
            return new Promise(function(resolve, reject) {
                if (vm.validate(file)) {
                    let fssApp = vm.tnx.fss.getAppName();
                    // fss单独部署，且上传目标即为fss服务，则需确保用户在fss服务中已登录
                    if (fssApp && vm.action.startsWith(vm.tnx.fss.getBaseUrl())) {
                        rpc.ensureLogined(function() {
                            resolve(file);
                        }, {
                            app: fssApp,
                            toLogin: function(loginFormUrl, originalUrl, originalMethod) {
                                // 此时已可知在CAS服务器上未登录，即未登录任一服务
                                reject(file);
                                // 从当前应用登录表单地址
                                rpc.get('/authentication/login-url', function(loginUrl) {
                                    if (loginUrl) {
                                        // 默认登录后跳转回当前页面
                                        loginUrl += loginUrl.contains('?') ? '&' : '?';
                                        loginUrl += rpc.loginSuccessRedirectParameter + '=' + window.location.href;
                                        rpc.toLogin(loginUrl, vm.action, 'POST');
                                    }
                                });
                                return true;
                            }
                        });
                    } else {
                        resolve(file);
                    }
                } else {
                    reject(file);
                }
            });
        },
        onProgress: function(event, file, fileList) {
            file.uploading = true;
            this._resizeFilePanel(file, fileList);
        },
        _resizeFilePanel: function(file, fileList) {
            const $container = $('#' + this.id);
            const $upload = $('.el-upload', $container);
            if (fileList.length >= this.uploadLimit.number) {
                // 隐藏添加按钮
                $upload.hide();
            }
            const fileId = this.getFileId(file);
            const $listItem = $('.el-upload-list__panel[data-file-id="' + fileId + '"]', $container).parent();
            let uploadStyle = $upload.attr('style');
            if (uploadStyle) {
                uploadStyle = uploadStyle.replace(/display:\s*none;/, ''); // 去掉隐藏样式
                $listItem.attr('style', uploadStyle);
            }
            if (typeof this.width === 'string' && this.width.endsWith('%')) {
                $listItem.parent().css({width: '100%'});
            }
            $listItem.parent().css({'min-height': $upload.outerHeight(true)});
        },
        _onSuccess: function(uploadedFile, file, fileList) {
            file.uploading = false;
            if (this.onSuccess) {
                this.onSuccess(uploadedFile, file, fileList);
            }
        },
        onError: function(error, file, fileList) {
            $('#' + this.id + ' .el-upload').show();
            let message = JSON.parse(error.message);
            if (message) {
                if (message.status === 500) {
                    this.tnx.app.rpc.handle500Error(message.message, {
                        error: this.handleErrors
                    });
                    return;
                } else if (message.errors) {
                    this.handleErrors(message.errors);
                    return;
                }
            }
            console.error(error.message);
        },
        removeFile: function(file) {
            this.uploadFiles.remove(function(f) {
                return file.uid === f.uid;
            });
            if (this.uploadFiles.length < this.uploadLimit.number) {
                let container = $('#' + this.id);
                // 去掉文件列表的宽度，以免其占高度
                $('.el-upload-list', container).css({
                    width: 'unset'
                });
                // 显示添加按钮
                $('.el-upload', container).show();
            }
            if (this.onRemoved) {
                this.onRemoved(file);
            }
        },
        previewFile: function(file) {
            if (!file.width || !file.height) {
                const image = new Image();
                image.src = file.previewUrl || file.url;
                const _this = this;
                image.onload = function() {
                    file.width = image.width;
                    file.height = image.height;
                    _this._doPreviewFile(file);
                }
            } else {
                this._doPreviewFile(file);
            }
        },
        _doPreviewFile: function(file) {
            const dialogPadding = 16;
            let top = (this.tnx.util.dom.getDocHeight() - file.height) / 2 - dialogPadding;
            top = Math.max(top, 5); // 最高顶部留5px空隙
            let width = file.width;
            width = Math.min(width, this.tnx.util.dom.getDocWidth() - 10); // 最宽两边各留10px空隙
            const content = '<img src="' + file.url + '" style="max-width: 100%;">';
            this.tnx.dialog(content, '', [], {
                top: top + 'px',
                width: width + 'px',
            });
        },
        size: function() {
            if (this.uploadFiles && this.uploadFiles.length) {
                return this.uploadFiles.length;
            }
            return 0;
        }
    }
}
</script>
