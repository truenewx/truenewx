// tnxcore.js
/**
 * 基于原生JavaScript的扩展支持
 */

import util from './tnxcore-util';
import app from './tnxcore-app';
import fss from './tnxcore-fss';
import wechat from './tnxcore-wechat';

const tnxcore = {
    libs: {},
    util: util,
    app: app,
    fss: fss,
    wechat: wechat,
    alert(message, title, callback) {
        if (typeof title === 'function') {
            callback = title;
            title = undefined;
        }
        const content = title ? (title + ':\n' + message) : message;
        alert(content);
        if (typeof callback === 'function') {
            callback();
        }
    },
    success(message, callback) {
        this.alert(message, '成功', callback);
    },
    error(message, callback) {
        this.alert(message, '错误', callback);
    },
    confirm(message, title, callback) {
        if (typeof title === 'function') {
            callback = title;
            title = undefined;
        }
        const yes = confirm(title + ':\n' + message);
        if (typeof callback === 'function') {
            callback(yes);
        }
    }
};

window.tnx = tnxcore;

export default tnxcore;
