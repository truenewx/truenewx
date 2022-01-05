// tnxel.js
/**
 * 基于ElementPlus的扩展支持
 */
import ElementPlus, {ElLoading, ElMessage, ElMessageBox} from 'element-plus';
import ElementPlus_zh_CN from 'element-plus/es/locale/lang/zh-cn';
import tnxvue from '../tnxvue.js';
import $ from 'jquery';

import Avatar from './avatar/Avatar';
import Alert from './alert/Alert';
import Button from './button/Button';
import CheckIcon from './check-icon/CheckIcon';
import Curd from './curd/Curd';
import DatePicker from './date-picker/DatePicker';
import DateRange from './date-range/DateRange';
import DetailForm from './detail-form/DetailForm';
import Dialog from './dialog/Dialog';
import Drawer from './drawer/Drawer';
import DropdownItem from './dropdown-item/DropdownItem';
import EnumSelect from './enum-select/EnumSelect';
import FetchCascader from './fetch-cascader/FetchCascader';
import FetchSelect from './fetch-select/FetchSelect';
import FetchTags from './fetch-tags/FetchTags';
import FssUpload from './fss-upload/FssUpload';
import FssView from './fss-view/FssView';
import Icon from './icon/Icon';
import InputNumber from './input-number/InputNumber';
import Paged from './paged/Paged';
import PermissionTree from './permission-tree/PermissionTree';
import QueryForm from './query-form/QueryForm';
import QueryTable from './query-table/QueryTable';
import RegionCascader from './region-cascader/RegionCascader';
import Select from './select/Select';
import Slider from './slider/Slider';
import StepsNav from './steps-nav/StepsNav';
import SubmitForm from './submit-form/SubmitForm';
import Tabs from './tabs/Tabs';
import Transfer from './transfer/Transfer';
import Upload from './upload/Upload';

const components = Object.assign({}, tnxvue.components, {
    Avatar,
    Alert,
    Button,
    CheckIcon,
    Curd,
    DatePicker,
    DateRange,
    DetailForm,
    Dialog,
    Drawer,
    DropdownItem,
    EnumSelect,
    FetchCascader,
    FetchSelect,
    FetchTags,
    FssUpload,
    FssView,
    Icon,
    InputNumber,
    Paged,
    PermissionTree,
    QueryForm,
    QueryTable,
    RegionCascader,
    Select,
    Slider,
    StepsNav,
    SubmitForm,
    Tabs,
    Transfer,
    Upload,
});

const dialogContainerClass = 'tnxel-dialog-container';
const drawerContainerClass = 'tnxel-drawer-container';

const tnxel = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {ElementPlus}),
    components,
    _dialogs: [], // 对话框堆栈
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
        $('body').append('<div class="' + dialogContainerClass + '" id="' + dialogId + '"></div>');
        if (!(buttons instanceof Array)) {
            buttons = [];
        }
        const containerSelector = '.' + dialogContainerClass + '#' + dialogId;
        options = options || {};
        const dialogVm = window.tnx.createVueInstance(componentDefinition, null, {
            container: containerSelector,
            title: title,
            content: content,
            contentProps: contentProps,
            buttons: buttons,
            theme: options.theme,
        }).mount(containerSelector);
        dialogVm.options = Object.assign(dialogVm.options || {}, options);
        dialogVm.options.onClosed = this.util.function.around(dialogVm.options.onClosed, function(onClosed) {
            let $container = $(containerSelector);
            $container.next('.el-overlay').remove();
            $container.remove();
            if (onClosed) {
                onClosed.call(dialogVm);
            }
        });
        this._dialogs.push(dialogVm);
        return dialogVm;
    },
    closeDialog(all, callback) {
        if (typeof all === 'function') {
            callback = all;
            all = false;
        }
        if (this._dialogs.length) {
            let dialog = this._dialogs.pop();
            while (dialog) {
                dialog.close(callback);
                if (all) {
                    dialog = this._dialogs.pop();
                } else {
                    break;
                }
            }
        }
    },
    _drawers: [], // 抽屉堆栈
    drawer(content, title, buttons, options, contentProps) {
        this._closeMessage();

        let componentOptions = {};
        if (this.util.isComponent(content)) {
            componentOptions.components = {
                'tnxel-drawer-content': content
            };
            content = null;
        }
        let componentDefinition = Object.assign({}, Drawer, componentOptions);

        const drawerId = 'drawer-' + (new Date().getTime());
        $('body').append('<div class="' + drawerContainerClass + '" id="' + drawerId + '"></div>');
        if (!(buttons instanceof Array)) {
            buttons = [];
        }
        const containerSelector = '.' + drawerContainerClass + '#' + drawerId;
        options = options || {};
        const drawerVm = window.tnx.createVueInstance(componentDefinition, null, {
            content: content,
            title: title,
            contentProps: contentProps,
            buttons: buttons,
            theme: options.theme,
        }).mount(containerSelector);
        drawerVm.id = drawerId;
        drawerVm.options = Object.assign(drawerVm.options || {}, options);
        drawerVm.options.onClosed = this.util.function.around(drawerVm.options.onClosed, function(onClosed) {
            let $container = $(containerSelector);
            $container.next('.el-overlay').remove();
            $container.remove();
            if (onClosed) {
                onClosed.call(drawerVm);
            }
        });
        this._drawers.push(drawerVm);
        return drawerVm;
    },
    closeDrawer(all, callback) {
        if (typeof all === 'function') {
            callback = all;
            all = false;
        }
        if (this._drawers.length) {
            let drawer = this._drawers.pop();
            while (drawer) {
                drawer.close(callback);
                if (all) {
                    drawer = this._drawers.pop();
                } else {
                    break;
                }
            }
        }
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
            confirmButtonText: '确定',
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
            confirmButtonText: '确定',
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
            confirmButtonText: '确定',
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
            confirmButtonText: '确定',
            cancelButtonText: '取消',
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
            customClass: 'tnxel-toast',
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
    vm.use(ElementPlus, {
        locale: ElementPlus_zh_CN,
    });
    install.call(window.tnx, vm);
});

tnxel.router.beforeLeave = tnxel.util.function.around(tnxel.router.beforeLeave, function(beforeLeave, router, from) {
    // 页面跳转前关闭当前页面中可能存在的所有消息框和对话框
    window.tnx._closeMessage();
    window.tnx.closeDialog(true);
    beforeLeave.call(window.tnx.router, router, from);
});

tnxel.date = {
    formatDateTime: function(row, column, cellValue) {
        if (cellValue) {
            return new Date(cellValue).formatDateTime();
        }
        return undefined;
    },
    formatDate: function(row, column, cellValue) {
        if (cellValue) {
            return new Date(cellValue).formatDate();
        }
        return undefined;
    },
    formatTime: function(row, column, cellValue) {
        if (typeof cellValue === 'number') {
            cellValue = new Date(cellValue);
        }
        if (cellValue instanceof Date) {
            cellValue = cellValue.formatTime();
        }
        if (typeof cellValue === 'string') {
            return cellValue;
        }
        return undefined;
    },
    formatTimeMinute: function(row, column, cellValue) {
        if (typeof cellValue === 'number') {
            cellValue = new Date(cellValue);
        }
        if (cellValue instanceof Date) {
            cellValue = cellValue.formatTimeMinute();
        }
        if (typeof cellValue === 'string') {
            let array = cellValue.split(':');
            if (array.length > 1) {
                return array[0] + ':' + array[1];
            }
        }
        return undefined;
    },
    formatDateMinute: function(row, column, cellValue) {
        if (cellValue) {
            return new Date(cellValue).formatDateMinute();
        }
        return undefined;
    },
    formatDateMonth: function(row, column, cellValue) {
        if (cellValue) {
            return new Date(cellValue).formatDateMonth();
        }
        return undefined;
    },
    formatPermanentableDate: function(row, column, cellValue) {
        if (Array.isArray(cellValue)) {
            cellValue = cellValue[column];
        }
        return tnxvue.util.date.formatPermanentableDate(cellValue);
    }
};

tnxel.number = {
    formatPercent: function(row, column, cellValue) {
        if (typeof cellValue !== 'number') {
            cellValue = parseFloat(cellValue);
        }
        if (!isNaN(cellValue)) {
            return cellValue.toPercent();
        }
        return undefined;
    }
}

tnxel.boolean = {
    items: {
        getText(type, value) {
            let items = this[type];
            if (Array.isArray(items)) {
                for (let item of items) {
                    if (item.value === value) {
                        return item.text;
                    }
                }
            }
            return undefined;
        },
        has: [{
            value: true,
            text: '有',
        }, {
            value: false,
            text: '无',
        }]
    },
    format: function(row, column, cellValue) {
        if (typeof cellValue === 'boolean') {
            cellValue = cellValue.toText();
        }
        return cellValue;
    },
    formatHas: function(row, column, cellValue) {
        if (typeof cellValue === 'boolean') {
            cellValue = tnxel.boolean.items.getText('has', cellValue);
        }
        return cellValue;
    }
}

const rpc = tnxel.app.rpc;
rpc.handleErrors = tnxel.util.function.around(rpc.handleErrors, function(handleErrors, errors, options) {
    if (options && options.form) {
        let forms;
        if (Array.isArray(options.form)) {
            forms = options.form;
        } else {
            forms = [options.form];
        }
        forms.forEach(form => {
            if (typeof form.disable === 'function') {
                form.disable(false);
            }
        });
    }
    return handleErrors.call(rpc, errors, options);
});

window.tnx = tnxel;

export default tnxel;
