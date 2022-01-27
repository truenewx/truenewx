// tnxel.js
/**
 * 基于Element的扩展支持
 */
import ElementUI, {Loading, Message, MessageBox} from 'element-ui';
import tnxvue from '../tnxvue.js';
import dialog from './dialog';
import $ from 'jquery';

import Alert from './alert';
import PermissionTree from './permission-tree';
import QueryForm from './query-form';
import SubmitForm from './submit-form';
import DetailForm from './detail-form';
import Select from './select';
import EnumSelect from './enum-select';
import FetchSelect from './fetch-select';
import FetchTags from './fetch-tags';
import FetchCascader from './fetch-cascader';
import RegionCascader from './region-cascader';
import Paged from './paged';
import QueryTable from './query-table';
import Transfer from './transfer';
import Avatar from './avatar';
import InputNumber from './input-number';
import StepsNav from './steps-nav';
import DatePicker from './date-picker';
import DateRange from './date-range';
import Upload from './upload';
import FssUpload from './fss-upload';
import FssView from './fss-view';
import Curd from './curd';
import Button from './button';
import Tabs from './tabs';
import TableColumn from './table-column';

const components = Object.assign({}, tnxvue.components, {
    Alert,
    PermissionTree,
    QueryForm,
    SubmitForm,
    DetailForm,
    Select,
    EnumSelect,
    FetchSelect,
    FetchTags,
    FetchCascader,
    RegionCascader,
    Paged,
    QueryTable,
    Transfer,
    Avatar,
    InputNumber,
    StepsNav,
    DatePicker,
    DateRange,
    Upload,
    FssUpload,
    FssView,
    Curd,
    Button,
    Tabs,
    TableColumn,
});

const tnxel = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {ElementUI}),
    components,
    install(Vue) {
        Vue.use(ElementUI);
        Object.keys(components).forEach(key => {
            const component = components[key];
            Vue.component(component.name, component);
        });
    },
    dialog() {
        this._closeMessage();
        return dialog.apply(this, arguments);
    },
    _closeMessage() {
        Message.closeAll();
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
        MessageBox.alert(message, title, options).then(callback);
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    success(message, callback, options) {
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'success',
        });
        this._closeMessage();
        MessageBox.alert(message, '成功', options).then(callback);
        this._handleZIndex('.el-message-box__wrapper:last');
    },
    error(message, callback, options) {
        options = Object.assign({
            dangerouslyUseHTMLString: true,
        }, options, {
            type: 'error',
        });
        this._closeMessage();
        MessageBox.alert(message, '错误', options).then(callback);
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
        const promise = MessageBox.confirm(message, title, options);
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
        Message(options);
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
        window.tnx.loadingInstance = Loading.service(options);
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

tnxel.table = {
    validateCellInput(inputElement, fieldRules, column) {
        return new Promise((resolve, reject) => {
            let fieldName = column.property;
            if (inputElement && fieldName && fieldRules?.length) {
                let rules = {};
                rules[fieldName] = fieldRules;
                let source = {};
                let $input = $(inputElement);
                source[fieldName] = $input.val();
                window.tnx.app.validator.validate(rules, source, function(errors) {
                    let _column = {
                        property: column.property,
                        label: column.label,
                        width: column.width,
                        required: typeof column.required === 'boolean' ? column.required
                            : fieldRules.contains(r => r.required),
                    };
                    if (errors?.length) {
                        $input.parent().addClass('is-error');
                        if (!$input.is('[readonly]')) {
                            $input.focus();
                        }

                        let fieldLabel = column.label || '';
                        _column.message = fieldLabel + errors[0].message;
                        reject(_column);
                    } else {
                        $input.parent().removeClass('is-error');
                        resolve(_column);
                    }
                });
            } else {
                resolve({
                    property: column.property,
                    label: column.label,
                    width: column.width,
                    required: column.required,
                });
            }
        });
    },
    loopValidatableInput(tableRef, rules, callback) {
        let columnNodes = tableRef.$slots.default;
        for (let columnIndex = 0; columnIndex < columnNodes.length; columnIndex++) {
            let columnNode = columnNodes[columnIndex];
            let props = columnNode.componentOptions?.propsData;
            if (props) {
                let fieldRules = rules[props.prop];
                if (fieldRules) {
                    let $trs = $('tbody > tr', tableRef.$el);
                    for (let tr of $trs) {
                        let $td = $(tr).children('td:eq(' + columnIndex + ')');
                        let inputElement = $('input', $td)[0];
                        if (inputElement) {
                            callback(inputElement, fieldRules, {
                                property: props.prop,
                                label: props.label,
                                width: props.width,
                                required: props.required,
                            });
                        }
                    }
                }
            }
        }
    },
    validateTableInputs(tableRef, rules) {
        let _this = this;
        return new Promise((resolve, reject) => {
            let validatableInputNum = 0;
            _this.loopValidatableInput(tableRef, rules, () => {
                validatableInputNum++;
            });

            let resolvedNum = 0;
            let rejected = false;
            _this.loopValidatableInput(tableRef, rules, (inputElement, fieldRules, column) => {
                _this.validateCellInput(inputElement, fieldRules, column).then(col => {
                    if (++resolvedNum >= validatableInputNum) {
                        resolve(col);
                    }
                }).catch(error => {
                    if (!rejected) {
                        rejected = true;
                        reject(error);
                    }
                });
            });
        });
    },
}

tnxel.libs.Vue.use(tnxel);

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
