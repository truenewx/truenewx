<template>
    <el-upload ref="upload" name="file" class="tnxel-upload-container"
        :class="{center: center, 'hide-file-list': !showFileList}"
        :id="id"
        :action="action"
        :before-upload="_beforeUpload"
        :on-progress="_onProgress"
        :on-success="_onSuccess"
        :on-error="_onError"
        :with-credentials="true"
        :list-type="listType"
        :file-list="fileList"
        :show-file-list="true"
        :headers="uploadHeaders"
        :multiple="uploadOptions ? uploadOptions.number > 1 : false"
        :accept="uploadAccept" :disabled="disabled" v-if="uploadOptions">
        <template #file="{file}">
            <div class="el-upload-list__panel" :data-file-id="getFileId(file)" :style="itemPanelStyle"
                v-if="showFileList">
                <img class="el-upload-list__item-thumbnail" :src="file.url" v-if="imageable">
                <div class="el-upload-list__item-name" v-else>
                    <tnxel-icon value="Document"/>
                    <span>{{ file.name }}</span>
                </div>
                <label class="el-upload-list__item-status-label">
                    <tnxel-icon value="CircleCheck" class="text-success" v-if="listType === 'text'"/>
                    <tnxel-icon value="Check" v-else/>
                </label>
                <span class="el-upload-list__item-uploading" v-if="file.uploading">
                    <tnxel-icon value="Loading"/>
                </span>
                <div class="el-upload-list__item-actions">
                    <div class="flex-center">
                        <tnxel-icon value="ZoomIn" @click="previewFile(file)" v-if="isPreviewable(file)"/>
                        <tnxel-icon value="Delete" @click="removeFile(file)"/>
                    </div>
                </div>
            </div>
        </template>
        <template #trigger>
            <el-tooltip :content="tipContent" placement="top" :disabled="tip !== 'tooltip'">
                <el-button class="upload-trigger" :title="tip === 'title' ? tipContent : undefined"
                    :disabled="disabled" v-if="listType === 'text'">
                    <tnxel-icon :value="icon" :size="uploadIconSize"/>
                    <div class="upload-trigger-text" v-if="triggerText">{{ triggerText }}</div>
                </el-button>
                <div class="upload-trigger" :title="tip === 'title' ? tipContent : undefined"
                    :disabled="disabled" v-else>
                    <tnxel-icon :value="icon" :size="uploadIconSize"/>
                    <div class="upload-trigger-text" v-if="triggerText">{{ triggerText }}</div>
                </div>
            </el-tooltip>
        </template>
        <template #tip v-if="tipContent && (typeof tip !== 'string')">
            <div class="el-upload__tip" v-text="tipContent"></div>
        </template>
    </el-upload>
</template>

<script>
import $ from 'jquery';
import Icon from '../icon/Icon';

export default {
    name: 'TnxelUpload',
    components: {
        'tnxel-icon': Icon,
    },
    props: {
        appName: String, // 上传目标应用名称
        action: {
            type: String,
            required: true,
        },
        uploadOptions: Object,
        fileList: {
            type: Array,
            default: () => [],
        },
        width: {
            type: [Number, String],
        },
        height: {
            type: [Number, String],
        },
        icon: {
            type: String,
            default: 'Plus',
        },
        iconSize: Number,
        triggerText: String,
        beforeUpload: Function,
        /**
         * 所有可上传的文件均已开始上传时的钩子
         */
        onUpload: Function,
        onProgress: Function,
        onSuccess: Function,
        onError: Function,
        onRemoved: Function,
        handleErrors: {
            type: Function,
            default(errors) {
                if (errors?.length) {
                    window.tnx.app.rpc.handleErrors(errors);
                }
            }
        },
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
        disabled: Boolean,
    },
    data() {
        const tnx = window.tnx;
        return {
            tnx: tnx,
            id: 'upload-container-' + tnx.util.string.random(32),
            tipMessages: {
                number: '一次最多上传{0}个文件',
                capacity: '单个文件不能超过{0}',
                extensions: '只能上传{0}文件',
                excludedExtensions: '不能上传{0}文件',
            },
            uploadHeaders: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            files: [], // 文件清单，包含初始文件和新增成功的文件，不包含校验失败的文件
        };
    },
    computed: {
        imageable() {
            let imageable = false;
            if (this.uploadOptions?.extensions) {
                for (let extension of this.uploadOptions.extensions) {
                    if (this.tnx.util.file.isImage(extension)) {
                        imageable = true;
                    } else {
                        return false;
                    }
                }
            }
            return imageable;
        },
        listType() {
            return this.imageable ? 'picture-card' : 'text';
        },
        tipContent() {
            let content = '';
            if (this.tip !== false && this.uploadOptions) {
                const separator = '，';
                if (this.uploadOptions.number > 1) {
                    content += separator + this.tipMessages.number.format(this.uploadOptions.number);
                }
                if (this.uploadOptions.capacity > 0) {
                    const capacity = this.tnx.util.string.getCapacityCaption(this.uploadOptions.capacity, 2);
                    content += separator + this.tipMessages.capacity.format(capacity);
                }
                if (this.uploadOptions.extensions && this.uploadOptions.extensions.length) {
                    const extensions = this.uploadOptions.extensions.join('、');
                    if (this.uploadOptions.extensionsRejected) {
                        content += separator + this.tipMessages.excludedExtensions.format(extensions);
                    } else {
                        content += separator + this.tipMessages.extensions.format(extensions);
                    }
                }
                if (content.length > 0) {
                    content = content.substr(separator.length);
                }
                if (this.uploadOptions.publicReadable) {
                    if (content.length) {
                        content += '；';
                    }
                    content += '该' + (this.imageable ? '图片' : '文件') + '可能对外公开，请慎重选择上传。';
                }
                if (typeof this.tip === 'function') {
                    content = this.tip(content);
                }
            }
            return content;
        },
        uploadAccept() {
            if (this.uploadOptions && !this.uploadOptions.extensionsRejected && this.uploadOptions.extensions?.length) {
                let accept = '';
                for (let extension of this.uploadOptions.extensions) {
                    accept += ',.' + extension;
                }
                return accept.substr(1);
            }
            return undefined;
        },
        uploadSize() {
            let width = this.width;
            let height = this.height;
            let uploadSize = undefined;
            if (this.uploadOptions.sizes && this.uploadOptions.sizes.length) {
                uploadSize = this.uploadOptions.sizes[0];
            }
            if (uploadSize) {
                width = width || uploadSize.width;
                height = height || uploadSize.height;
            }
            return {width, height};
        },
        itemPanelStyle() {
            let style = {
                height: window.tnx.util.string.getPixelString(this.uploadSize.height),
            }
            if (this.imageable) {
                style.width = window.tnx.util.string.getPixelString(this.uploadSize.width);
            }
            return style;
        },
        uploadIconSize() {
            if (this.iconSize) {
                return this.iconSize;
            }
            let width = this.uploadSize.width || 0;
            let height = this.uploadSize.height || 0;
            let iconSize = Math.floor(Math.min(width, height) / 3);
            iconSize = Math.max(16, Math.min(iconSize, 32));
            return iconSize;
        },
    },
    watch: {
        uploadOptions() {
            this.init();
        },
        fileList() {
            if (this.uploadOptions?.number) {
                this.init();
            }
        }
    },
    methods: {
        init() {
            this.files = [].concat(this.fileList);
            let vm = this;
            // 需在vue渲染之后才可正常操作dom元素
            this.$nextTick(function() {
                // 初始化显示尺寸
                let width = vm.uploadSize.width;
                if (width) {
                    if (typeof width === 'string' && !width.endsWith('%')) {
                        width = window.tnx.util.string.getPixelNumber(width);
                    }
                    if (typeof width === 'number') {
                        width += 2; // 加上边框宽度
                    }
                    width = window.tnx.util.string.getPixelString(width);
                }
                let height = vm.uploadSize.height;
                if (height) {
                    if (typeof height === 'string' && !height.endsWith('%')) {
                        height = window.tnx.util.string.getPixelNumber(height);
                    }
                    if (typeof height === 'number') {
                        height += 2; // 加上边框宽度
                    }
                    height = window.tnx.util.string.getPixelString(height);
                }

                let $container = $('#' + vm.id);
                let $upload = $('.el-upload', $container);
                $upload.css({
                    width: width,
                    height: height,
                });

                // 不显示文件清单，或文件数量未达到上限，则显示添加框
                if (!vm.showFileList || vm.files.length < vm.uploadOptions.number) {
                    $upload.css({
                        display: 'inline-flex'
                    });
                }
                // 构建初始化文件显示面板
                if (vm.fileList) {
                    for (let file of vm.fileList) {
                        vm._resizeFilePanel(file, vm.fileList);
                    }
                }
            });
        },
        getFileId(file) {
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
        validate(file) {
            // 在检查首个准备上传的文件前，清空可能存在的错误消息
            if (this.files.length === 0) {
                this.handleErrors([]);
            }
            // 校验文件重复
            const vm = this;
            if (this.files.contains(function(f) {
                const raw = f.raw ? f.raw : f;
                return file.uid !== raw.uid && vm.getFileId(file) === vm.getFileId(raw);
            })) {
                return false;
            }
            // 校验数量
            if (this.files.length >= this.uploadOptions.number) {
                let message = this.tipMessages.number.format(this.uploadOptions.number);
                message += '，多余的文件未加入上传队列';
                this.handleErrors([{
                    code: 'error.upload.number',
                    message: message,
                }]);
                return false;
            }
            // 校验容量大小
            if (this.uploadOptions.capacity > 0 && file.size > this.uploadOptions.capacity) {
                const capacity = this.tnx.util.string.getCapacityCaption(this.uploadOptions.capacity);
                let message = this.tipMessages.capacity.format(capacity, 2);
                message += '，文件"' + file.name + '"大小为' + this.tnx.util.string.getCapacityCaption(file.size, 2)
                    + '，不符合要求';
                this.handleErrors([{
                    code: 'error.upload.capacity',
                    message: message,
                }]);
                return false;
            }
            // 校验扩展名
            if (this.uploadOptions.extensions && this.uploadOptions.extensions.length) {
                const extension = file.name.substr(file.name.lastIndexOf('.') + 1);
                if (this.uploadOptions.extensionsRejected) { // 扩展名排除模式
                    if (this.uploadOptions.extensions.containsIgnoreCase(extension)) {
                        const extensions = this.uploadOptions.extensions.join('、');
                        let message = this.tipMessages.excludedExtensions.format(extensions);
                        this.handleErrors([{
                            code: 'error.upload.extension',
                            message: message,
                        }]);
                        return false;
                    }
                } else { // 扩展名包含模式
                    if (!this.uploadOptions.extensions.containsIgnoreCase(extension)) {
                        const extensions = this.uploadOptions.extensions.join('、');
                        let message = this.tipMessages.extensions.format(extensions);
                        message += '，文件"' + file.name + '"不符合要求';
                        this.handleErrors([{
                            code: 'error.upload.extension',
                            message: message,
                        }]);
                        return false;
                    }
                }
            }
            // 校验通过才加入上传中文件清单
            this.files.push(file);
            return true;
        },
        _beforeUpload(file) {
            const vm = this;
            const rpc = this.tnx.app.rpc;
            return new Promise(function(resolve, reject) {
                if (vm.validate(file)) {
                    let $upload = $('#' + vm.id + ' .el-upload');
                    if (vm.showFileList && vm.files.length >= vm.uploadOptions.number) {
                        $upload.css('visibility', 'hidden');
                    }

                    // 上传前需确保用户在fss应用中已登录
                    rpc.ensureLogined(function() {
                        if (vm.beforeUpload) {
                            let promise = vm.beforeUpload(file);
                            if (promise instanceof Promise) {
                                promise.then(function() {
                                    resolve(file);
                                }).catch(function() {
                                    reject(file);
                                });
                            } else if (promise === false) {
                                reject(file);
                            } else {
                                resolve(file);
                            }
                        } else {
                            resolve(file);
                        }
                    }, {
                        app: vm.appName,
                        toLogin(loginFormUrl, originalUrl, originalMethod) {
                            $upload.css('visibility', 'unset');
                            // 从当前应用登录表单地址
                            rpc.get('/authentication/login-url', function(loginUrl) {
                                if (loginUrl) {
                                    // 默认登录后跳转回当前页面
                                    loginUrl += loginUrl.contains('?') ? '&' : '?';
                                    loginUrl += rpc.loginSuccessRedirectParameter + '=' + window.location.href;
                                    rpc.toLogin(loginUrl, vm.action, 'POST');
                                } else {
                                    // 获取登录地址为空，则说明实际上是登录状态
                                    resolve(file);
                                }
                            });
                            return true;
                        }
                    });
                } else {
                    reject(file);
                }
            });
        },
        _onProgress(event, file, fileList) {
            file.uploading = true;
            if (file.percentage === 0) { // 首次执行
                this._resizeFilePanel(file, fileList);
                if (this.onUpload) {
                    // 最后一个文件开始上传，触发onUpload事件处理
                    if (file.uid === fileList[fileList.length - 1].uid) {
                        let resolvedNum = fileList.length;
                        let rejectedNum = this.files.length - resolvedNum;
                        this.onUpload(resolvedNum, rejectedNum);
                    }
                }
            } else if (this.onProgress) {
                this.onProgress(file);
            }
        },
        _resizeFilePanel(file, fileList) {
            const $container = $('#' + this.id);
            const $upload = $('.el-upload', $container);
            // 显示文件清单且文件数量已达上限，则隐藏添加框
            if (this.showFileList && this.files.length >= this.uploadOptions.number) {
                // 隐藏添加框
                $upload.css({
                    display: 'none',
                    visibility: 'unset',
                });
            }

            let fileId = this.getFileId(file);
            let $fileItem = $('.el-upload-list__panel[data-file-id="' + fileId + '"]', $container).parent();
            let uploadStyle = $upload.attr('style');
            if (uploadStyle) {
                // 去掉隐藏样式
                uploadStyle = uploadStyle.replace(/display:\s*none;/, '').trim();
                $fileItem.attr('style', uploadStyle);
            }
        },
        _onSuccess(result, file, fileList) {
            file.uploading = false;
            if (this.onSuccess) {
                this.onSuccess(result, file, fileList);
            }
        },
        _onError(error, file, fileList) {
            $('#' + this.id + ' .el-upload').show();
            let message;
            try {
                message = JSON.parse(error.message);
            } catch (e) {
                // 忽略JSON解析错误
            }
            if (message) {
                if (message.status === 500) {
                    if (this.onError) {
                        this.onError(file, message.message);
                    } else {
                        this.tnx.app.rpc.handle500Error(message.message, {
                            error: this.handleErrors
                        });
                    }
                    return;
                } else if (message.errors) {
                    if (this.onError) {
                        let errorMessage = '';
                        for (let error of message.errors) {
                            errorMessage += error.message + '\n';
                        }
                        errorMessage = errorMessage.trim();
                        this.onError(file, errorMessage);
                    } else {
                        this.handleErrors(message.errors);
                    }
                    return;
                }
            }
            console.error(error.message);
            if (this.onError) {
                this.onError(file, error.message);
            }
        },
        removeFile(file) {
            this.files.remove(function(f) {
                return file.uid === f.uid;
            });
            if (this.files.length < this.uploadOptions.number) {
                let container = $('#' + this.id);
                this.$nextTick(function() {
                    // 去掉文件列表的样式，以免其占高度
                    $('.el-upload-list', container).removeAttr('style');
                    // 恢复添加框默认样式
                    $('.el-upload', container).css('display', 'inline-flex');
                });
            }
            if (this.onRemoved) {
                this.onRemoved(file);
            }
        },
        previewFile(file) {
            let extension = this.getExtension(file);
            if (extension === 'pdf') {
                let url = this.tnx.util.net.appendParams(file.previewUrl, {
                    inline: true
                });
                window.open(url);
            } else {
                if (!file.width || !file.height) {
                    const image = new Image();
                    image.src = file.previewUrl || file.url;
                    const _this = this;
                    image.onload = function() {
                        file.width = image.width;
                        file.height = image.height;
                        _this._doPreviewImage(file);
                    }
                } else {
                    this._doPreviewImage(file);
                }
            }
        },
        _doPreviewImage(file) {
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
        size() {
            if (this.files && this.files.length) {
                return this.files.length;
            }
            return 0;
        },
        getExtension(file) {
            let extension = this.tnx.util.net.getExtension(file.name);
            if (extension) {
                return extension.toLowerCase();
            }
            return '';
        },
        isPreviewable(file) {
            let extension = this.getExtension(file);
            return ['jpg', 'png', 'gif', 'svg', 'pdf'].contains(extension);
        },
    }
}
</script>

<style>
.tnxel-upload-container {
    display: flex;
    flex-direction: column;
}

.tnxel-upload-container.center {
    align-items: center;
}

.tnxel-upload-container.hide-file-list .el-upload-list__item,
.tnxel-upload-container.hide-file-list .el-upload-list--text {
    display: none;
}

.tnxel-upload-container .el-upload {
    border-radius: .25rem;
    display: none;
    align-items: center;
    justify-content: center;
    margin-bottom: 0.5rem;
    width: fit-content;
    height: fit-content;
    background-color: transparent;
}

.tnxel-upload-container .el-upload.el-upload--text {
    order: -1; /* 排在提示文本前 */
}

.tnxel-upload-container:not(.center) .el-upload.el-upload--text {
    justify-content: unset;
}

.tnxel-upload-container .el-upload:hover {
    color: inherit;
}

.tnxel-upload-container .upload-trigger {
    display: flex;
    align-items: center;
    min-height: 32px;
    color: var(--el-text-color-regular);
}

.tnxel-upload-container div.upload-trigger {
    margin: 0 0.5rem;
}

.tnxel-upload-container .upload-trigger-text {
    margin-left: 0.25rem;
    line-height: 1rem;
    font-size: 14px;
}

.tnxel-upload-container.center .el-upload {
    margin-right: auto;
    margin-left: auto;
}

.tnxel-upload-container.center upload-trigger {
    flex-direction: column;
}

.tnxel-upload-container.center .upload-trigger-text {
    margin-left: 0;
    margin-top: 0.25rem;
}

.tnxel-upload-container .el-upload i {
    margin-top: 0;
    color: inherit;
}

.tnxel-upload-container .el-upload-list--text {
    order: -2; /* 排在添加按钮前 */
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item-name {
    display: flex;
    align-items: center;
    margin-right: 0.5rem;
    padding: 0;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item-name i {
    margin: 0;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item-status-label {
    display: flex;
    align-items: center;
    position: unset;
    margin-left: 24px;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item-status-label i {
    font-size: 1rem;
    margin-left: 0.25rem;
    margin-right: 0.25rem;
}

.tnxel-upload-container .el-upload-list--picture-card {
    display: inline-flex;
    align-items: center;
    max-width: 100%;
}

.tnxel-upload-container.center .el-upload-list--picture-card {
    justify-content: center;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item {
    transition: none;
    display: flex;
    align-items: center;
    margin-top: 0;
    margin-bottom: 0.5rem;
    border: 1px solid var(--el-border-color);
    border-radius: .25rem;
    width: fit-content;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item .el-upload-list__item-actions {
    display: none;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__item:focus .el-upload-list__item-actions,
.tnxel-upload-container .el-upload-list--text .el-upload-list__item:active .el-upload-list__item-actions,
.tnxel-upload-container .el-upload-list--text .el-upload-list__item:hover .el-upload-list__item-actions {
    display: unset;
}

.tnxel-upload-container .el-upload-list__panel {
    min-width: 32px;
    min-height: 32px;
}

.tnxel-upload-container .el-upload-list--text .el-upload-list__panel {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 0 0.5rem;
}

.tnxel-upload-container .el-upload-list--picture-card .el-upload-list__item {
    transition: none;
    border-radius: .25rem;
    width: unset;
    height: unset;
    line-height: 0;
}

.tnxel-upload-container .el-upload-list__item-thumbnail {
    object-fit: contain;
}

.tnxel-upload-container .el-upload-list__item-actions {
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 3rem;
}

.tnxel-upload-container .el-upload-list__item-actions i {
    cursor: pointer;
    margin-left: 0.25rem;
    margin-right: 0.25rem;
}

.tnxel-upload-container .el-upload-list__item-uploading {
    position: absolute;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
    color: white;
    opacity: 0.5;
    background-color: black;
}

.tnxel-upload-container .el-upload__tip {
    line-height: 1;
    margin-top: 0;
}

.tnxel-upload-container.center .el-upload__tip {
    text-align: center;
}

.el-dropdown-menu__item .tnxel-upload-container {
    width: 100%;
}

.el-dropdown-menu__item .tnxel-upload-container .el-upload {
    margin-bottom: 0;
    border: none;
    justify-content: unset;
}

.el-dropdown-menu__item:hover .tnxel-upload-container .el-upload .upload-trigger,
.el-dropdown-menu__item:focus .tnxel-upload-container .el-upload .upload-trigger {
    color: var(--el-dropdown-menuItem-hover-color);
}

.el-dropdown-menu__item .tnxel-upload-container .el-upload .upload-trigger {
    min-height: 0;
    margin: 0;
}

.el-dropdown-menu__item .tnxel-upload-container .el-upload .upload-trigger-text {
    margin-left: 2px;
}
</style>
