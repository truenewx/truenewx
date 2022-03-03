/**
 * 基于WeUI的扩展支持
 */
import tnxvue from '../tnxvue.js';
import weui from 'weui.js';

import Button from './button/Button';
import Cells from './cells/Cells';
import EnumPicker from './enum-picker/EnumPicker';
import FormItem from './form-item/FormItem';
import FormItemError from './form-item-error/FormItemError';
import FormItemGroup from './form-item-group/FormItemGroup';
import Input from './input/Input';
import Msg from './msg/Msg';
import Picker from './picker/Picker';
import SubmitForm from './submit-form/SubmitForm';

window.weui = weui;

const tnxvw = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {
        weui
    }),
    components: Object.assign({}, tnxvue.components, {
        Button,
        Cells,
        EnumPicker,
        FormItem,
        FormItemError,
        FormItemGroup,
        Input,
        Msg,
        Picker,
        SubmitForm,
    }),
    icons: {
        success: 'weui-icon-success',
        info: 'weui-icon-info text-info',
        warning: 'weui-icon-warn text-warning',
        error: 'weui-icon-warn',
    },
    alertInstance: null,
    _closeMessage() {
        if (this.alertInstance) {
            this.alertInstance.hide();
            this.alertInstance = null;
        }
        this.hideLoading();
    },
    alert(message, title, callback, options) {
        this._closeMessage();

        if (typeof title === 'function') {
            options = callback;
            callback = title;
            title = '提示';
        }
        let iconClass;
        if (options?.type) {
            iconClass = this.icons[options.type];
            delete options.type;
        }
        iconClass = iconClass || this.icons.info;
        let content = '<div class="d-flex">'
            + '<div><i class="' + iconClass + '"></i></div>'
            + '<div style="margin-left: 4px; word-break: break-all;">' + message + '</div>'
            + '</div>';
        this.alertInstance = weui.alert(content, callback, Object.assign({}, options, {
            title: title,
        }));
    },
    success(message, callback, options) {
        this.alert(message, '成功', callback, Object.assign({}, options, {
            type: 'success'
        }));
    },
    error(message, callback, options) {
        this.alert(message, '错误', callback, Object.assign({}, options, {
            type: 'error'
        }));
    },
    toast(message, timeout, callback, options) {
        this._closeMessage();

        if (typeof timeout === 'function') {
            options = callback;
            callback = timeout;
            timeout = undefined;
        }
        options = Object.assign({}, options, {
            duration: timeout,
            callback: callback || function() {
                // weui存在bug，不提供callback会报错
            },
        });
        weui.toast(message, options);
    },
    loadingInstance: null,
    showLoading(message, options) {
        this._closeMessage();

        this.loadingInstance = weui.loading(message, options);
    },
    hideLoading(callback) {
        if (this.loadingInstance) { // 确保绝对的单例
            this.loadingInstance.hide(callback);
            this.loadingInstance = null;
        }
    },
    closeLoading(callback) {
        this.hideLoading(callback);
    },
});

tnxvw.libs.Vue.use(tnxvw);

tnxvw.app.rpc.handleOtherError = function(message) {
    window.tnx._closeMessage();
    weui.topTips(message);
    console.error(message);
}

window.tnx = tnxvw;

export default tnxvw;
