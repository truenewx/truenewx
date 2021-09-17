// tnxel.js
/**
 * 基于ElementPlus的扩展支持
 */
import ElementPlus, {ElLoading, ElMessage, ElMessageBox} from 'element-plus';
import tnxvue from '../tnxvue.js';
import $ from 'jquery';

import Icon from './icon/Icon';
import Avatar from './avatar/Avatar';
import Alert from './alert/Alert';
import Button from './button/Button';
import Dialog from './dialog/Dialog';
import Select from './select/Select';
import SubmitForm from './submit-form/SubmitForm';

const components = Object.assign({}, tnxvue.components, {
    Icon, Avatar, Alert, Button, Dialog, Select, SubmitForm,
});

const tnxel = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {ElementPlus}),
    components,
    dialog(content, title, buttons, options, contentProps) {
        this._closeMessage();

        let componentOptions = {};
        if (this.util.isComponent(content)) {
            componentOptions.components = {
                'tnxel-dialog-content': content
            };
            content = null;
        }
        let componentDefinition = Object.assign({}, Dialog, componentOptions);

        const dialogId = 'dialog-' + (new Date().getTime());
        $('body').append('<div id="' + dialogId + '"></div>');
        if (!(buttons instanceof Array)) {
            buttons = [];
        }
        const dialog = window.tnx.createVueInstance(componentDefinition, {
            content: content,
            title: title,
            contentProps: contentProps,
            buttons: buttons,
            theme: options.theme,
        }).mount('#' + dialogId);
        dialog.options = Object.assign(dialog.options || {}, options);
        return dialog;
    },
    _closeMessage() {
        ElMessage.closeAll();
        this.closeLoading();
    },
    _handleZIndex(selector) {
        const util = this.util;
        setTimeout(function() {
            const topZIndex = util.dom.minTopZIndex(2);
            const element = $(selector);
            const zIndex = Number(element.css('zIndex'));
            if (isNaN(zIndex) || topZIndex > zIndex) {
                element.css('zIndex', topZIndex);
                const modal = element.next();
                if (modal.is('.v-modal')) {
                    modal.css('zIndex', topZIndex - 1);
                }
            }
        });
    },
    alert(message, title, callback, options) {
        if (typeof title === 'function') {
            options = callback;
            callback = title;
            title = '提示';
        }
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'warning',
        });
        this._closeMessage();
        ElMessageBox.alert(message, title, options).then(callback);
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    success(message, callback, options) {
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'success',
        });
        this._closeMessage();
        ElMessageBox.alert(message, '成功', options).then(callback);
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    error(message, callback, options) {
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'error',
        });
        this._closeMessage();
        ElMessageBox.alert(message, '错误', options).then(callback);
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    confirm(message, title, callback, options) {
        if (typeof title === 'function') {
            options = callback;
            callback = title;
            title = '确认';
        }
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'info',
            iconClass: 'el-icon-question',
        });
        this._closeMessage();
        const promise = ElMessageBox.confirm(message, title, options);
        if (typeof callback === 'function') {
            promise.then(function() {
                callback(true);
            }).catch(function() {
                callback(false);
            });
        }
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    toast(message, timeout, callback, options) {
        if (typeof timeout === 'function') {
            options = callback;
            callback = timeout;
            timeout = undefined;
        }
        options = Object.assign({
            type: 'success', // 默认为成功主题，可更改为其它主题
            offset: this.util.dom.getDocHeight() * 0.4,
            dangerouslyUseHTMLString: true,
        }, options, {
            center: true, // 因为是竖向排列，所以必须居中
            showClose: false,
            message: message,
            duration: timeout || 1500,
            onClose: callback,
        });
        this._closeMessage();
        ElMessage(options);
        this._handleZIndex('.el-message:last');
    },
    showLoading(message, options) {
        if (typeof message !== 'string') {
            options = message;
            message = undefined;
        }
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            text: message,
        });
        this._closeMessage();
        window.tnx.loadingInstance = ElLoading.service(options);
        this._handleZIndex('.el-loading-mask');
    },
    closeLoading() {
        if (window.tnx.loadingInstance) { // 确保绝对的单例
            window.tnx.loadingInstance.close();
            window.tnx.loadingInstance = undefined;
        }
    },
    hideLoading() {
        this.closeLoading();
    },
    validateUploaded(vm, reject) {
        let result = true;
        let formRef = null;
        let refKeys = Object.keys(vm.$refs);
        for (let refKey of refKeys) {
            let refObj = vm.$refs[refKey];
            if (Array.isArray(refObj)) {
                for (let ref of refObj) {
                    if (typeof ref.validateUploaded === 'function') {
                        if (ref.validateUploaded(reject) === false) {
                            result = false;
                            break;
                        }
                    }
                }
            } else if (refObj.$el.tagName === 'FORM' && typeof refObj.disable === 'function') {
                formRef = refObj;
            } else {
                if (typeof refObj.validateUploaded === 'function') {
                    if (refObj.validateUploaded(reject) === false) {
                        result = false;
                        break;
                    }
                }
            }
        }
        if (!result && formRef) {
            formRef.disable(false);
        }
        return result;
    }
});

tnxel.install = tnxel.util.function.around(tnxel.install, function(install, vm) {
    vm.use(ElementPlus);
    install.call(window.tnx, vm);
});

window.tnx = tnxel;

export default tnxel;
