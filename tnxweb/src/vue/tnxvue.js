// tnxvue.js
/**
 * 基于Vue 3的扩展支持
 */
import tnxcore from '../tnxcore.js';
import validator from './tnxvue-validator';
import createRouter from './tnxvue-router';
import Text from './text/Text';
import Percent from './percent/Percent';
import CaptchaVerify from './aj-captcha/Verify';
import * as Vue from 'vue';
import mitt from 'mitt';
import './tnxvue.css';

const components = {
    Div: {
        name: 'TnxvueDiv',
        template: '<div><slot></slot></div>'
    },
    Span: {
        name: 'TnxvueSpan',
        template: '<span><slot></slot></span>'
    },
    Text,
    Percent,
    CaptchaVerify,
};

const tnxvue = Object.assign({}, tnxcore, {
    libs: Object.assign({}, tnxcore.libs, {
        Vue: Vue
    }),
    components,
    router: {
        instance: null,
        create(VueRouter, menu, fnImportPage) {
            return createRouter(VueRouter, menu, fnImportPage);
        },
        beforeLeave(router, from) {
            window.tnx.app.page.stopCache(router, from.path);
        }
    },
    createVueInstance(rootComponent, router, rootProps) {
        let vm = Vue.createApp(rootComponent, rootProps);
        vm.use(this);
        if (router) {
            vm.use(router);
            router.app = window.tnx.app;
            window.tnx.router.instance = vm.config.globalProperties.$router;
        } else if (window.tnx.router.instance) {
            vm.config.globalProperties.$router = window.tnx.router.instance;
        }
        vm.config.unwrapInjectedRef = true;
        window.tnx.app.eventBus = mitt();
        window.tnx.app.eventBus.once = function(name, handler) {
            this.all.set(name, [handler]);
        }
        return vm;
    },
    install(vm) {
        for (let key of Object.keys(this.components)) {
            const component = this.components[key];
            vm.component(component.name, component);
        }
    },
    dialog(content, title, buttons, options, contentProps) {
        // 默认不实现，由UI框架扩展层实现
        throw new Error('Unsupported function');
    },
    drawer(content, title, buttons, options, contentProps) {
        // 默认不实现，由UI框架扩展层实现
        throw new Error('Unsupported function');
    },
    open(component, props, options) {
        options = options || {};

        let mode = options.mode;
        if (component.methods?.dialog) {
            options = Object.assign({}, component.methods.dialog(props), options);
        } else if (component.methods?.drawer) {
            options = Object.assign({}, component.methods.drawer(props), options);
            mode = 'drawer';
        }

        const title = component.title || options.title;
        const buttons = options.buttons || this.getDefaultDialogButtons(options.type, options.click, options.theme);
        if (options.buttonText) {
            if (!Array.isArray(options.buttonText)) {
                options.buttonText = [options.buttonText];
            }
            for (let i = 0; i < buttons.length; i++) {
                let buttonText = options.buttonText[i];
                if (buttonText) {
                    buttons[i].text = buttonText;
                }
            }
        }
        delete options.mode;
        delete options.title;
        delete options.type;
        delete options.click;
        if (mode === 'drawer') {
            return this.drawer(component, title, buttons, options, props);
        }
        return this.dialog(component, title, buttons, options, props);
    },
    getDefaultDialogButtons(type, callback, theme) {
        if (callback !== false) {
            if (type === 'none') {
                return [];
            } else if (type === 'confirm') {
                return [{
                    text: '取消',
                    click(close) {
                        if (typeof callback === 'function') {
                            return callback.call(this, false, close);
                        }
                    }
                }, {
                    text: '确定',
                    type: theme || 'primary',
                    click(close) {
                        if (typeof callback === 'function') {
                            return callback.call(this, true, close);
                        }
                    }
                }];
            } else {
                return [{
                    text: '确定',
                    type: theme || 'primary',
                    click(close) {
                        if (typeof callback === 'function') {
                            return callback.call(this, close);
                        }
                    }
                }];
            }
        }
        return [];
    },
});

Object.assign(tnxvue.util, {
    /**
     * 判断指定对象是否组件实例
     * @param obj 对象
     * @returns {boolean} 是否组件实例
     */
    isComponent: function(obj) {
        return (typeof obj === 'object') && (typeof obj.render === 'function');
    }
});

tnxvue.app.isProduction = function() {
    if (process && process.env && process.env.NODE_ENV !== 'production') {
        return false;
    }
    return true;
};

tnxvue.app.toDevUrl = function(url, portIndex, replacement) {
    if (!this.isProduction()) {
        let index = url.indexOf(':', url.indexOf('//'));
        if (index > 0) { // 必须带有端口号才可替换
            let prefix = url.substring(0, index + 1); // 端口号之前的部分
            portIndex = portIndex || 1; // 开发环境端口与正式环境端口不同点的位置，如：8080之于8880，则portIndex为1
            replacement = replacement || '0'; // 开发环境端口在不同于正式环境端口位置要替代的值，如8080之于8880，则replacement为'0'

            let path = ''; // 路由路径
            let wellIndex = url.indexOf('#');
            if (wellIndex > 0) { // 如果有路由路径，则将url拆成两部分，以便于后续处理
                path = url.substring(wellIndex);
                url = url.substring(0, wellIndex);
            }
            // 开发环境路径不包含contextPath，去掉url中的contenxtPath
            let replaceEndIndex = prefix.length + portIndex + 1;
            let slashIndex = url.indexOf('/', prefix.length);
            url = url.substring(0, prefix.length + portIndex) + replacement
                + url.substring(replaceEndIndex, slashIndex > replaceEndIndex ? slashIndex : undefined) + path;
        }
    }
    return url;
}

// 元数据到async-validator组件规则的转换处理
tnxvue.app.validator = validator;
tnxvue.app.rpc.getMeta = tnxvue.util.function.around(tnxvue.app.rpc.getMeta, function(getMeta, url, callback, app) {
    getMeta.call(tnxvue.app.rpc, url, function(meta) {
        if (meta) { // meta已被缓存，所以直接修改其内容，以便同步缓存
            meta.$rules = validator.getRules(meta);
            if (typeof callback === 'function') {
                callback.call(this, meta);
            }
        }
    }, app);
});

tnxvue.app.page.init = tnxvue.util.function.around(tnxvue.app.page.init, function(init, page, container) {
    if (container.tagName === 'BODY') { // vue不推荐以body为挂载目标，故从body下获取第一个div作为容器
        for (let i = 0; i < container.children.length; i++) {
            const child = container.children[i];
            if (child.tagName === 'DIV') {
                container = child;
                break;
            }
        }
    }
    init.call(this, page, container);
});

Object.assign(tnxvue.app.page, {
    startCache: function(router, model, intervalMillis, ignoredFields) {
        if (localStorage && intervalMillis && intervalMillis > 1000) { // 缓存间隔必须超过1秒
            let path = this._readCache(router, undefined, function(cache) {
                Object.assign(model, cache.model);
            });

            if (path) {
                let _this = this;
                let intervalId = setInterval(function() {
                    _this._storeCache(router, path, intervalId, model, ignoredFields);
                }, intervalMillis);
            }
        }
        return model;
    },
    _readCache: function(router, path, callback) {
        if (localStorage) {
            path = path || router.app.$route.path || '/';
            let cache = localStorage[path];
            if (cache) {
                cache = window.tnx.util.string.parseJson(cache);
                if (typeof callback === 'function') {
                    callback.call(this, cache);
                }
            }
            return path;
        }
    },
    _storeCache: function(router, path, intervalId, model, ignoredFields) {
        if (path && intervalId) {
            let data = {};
            if (Array.isArray(ignoredFields) && ignoredFields.length) {
                Object.keys(model).forEach(key => {
                    if (!ignoredFields.contains(key)) {
                        data[key] = model[key];
                    }
                });
            } else {
                data = model;
            }
            localStorage[path] = tnxvue.util.string.toJson({
                intervalId: intervalId,
                model: data,
                ignored: ignoredFields,
            });
        }
    },
    saveCache: function(router, model) {
        let intervalId;
        let ignoredFields;
        let path = this._readCache(router, undefined, function(cache) {
            intervalId = cache.intervalId;
            ignoredFields = cache.ignored;
        });
        this._storeCache(router, path, intervalId, model, ignoredFields);
    },
    stopCache: function(router, path) {
        return this._readCache(router, path, function(cache) {
            clearInterval(cache.intervalId);
        });
    },
    clearCache: function(router) {
        let path = this.stopCache(router);
        if (path) {
            delete localStorage[path];
        }
    },
    /**
     * 前端页面模型转换为后端命令模型，检查文件上传是否完成，去掉后端不需要的多余字段，转换多层嵌入字段数据使其符合服务端命令模型的基本要求
     * @param model 前端页面模型
     * @param refs 页面中的组件引用集
     * @param validFieldNames 有效的字段名称集，如有指定则清除模型中的无效字段
     */
    toCommandModel: function(vm, model, validFieldNames) {
        let result = {};
        if (model) {
            if (vm.$refs) {
                let refKeys = Object.keys(vm.$refs);
                for (let refKey of refKeys) {
                    let ref = vm.$refs[refKey];
                    if (typeof ref.getLocationUrl === 'function') {
                        if (ref.validateUploaded() === false) {
                            return null;
                        }
                    }
                }
            }
            let fieldNames = Object.keys(model);
            for (let fieldName of fieldNames) {
                if (!validFieldNames || !validFieldNames.length || validFieldNames.contains(fieldName)) {
                    if (fieldName.contains('__')) {
                        let path = fieldName.replace('__', '.');
                        tnxvue.util.object.setValue(result, path, model[fieldName]);
                    } else {
                        result[fieldName] = model[fieldName];
                    }
                }
            }
        }
        return result;
    },
    /**
     * 转换多层嵌入字段数据使其符合前端页面模型的基本要求
     * @param model 服务端视图模型
     */
    toPageModel: function(model) {
        let expanded = this._expandRefFields(model);
        while (expanded) {
            expanded = this._expandRefFields(model);
        }
        return model;
    },
    _expandRefFields: function(model) {
        let expanded = false;
        Object.keys(model).forEach(key => {
            let value = model[key];
            if (value && typeof value === 'object') {
                Object.keys(value).forEach(refKey => {
                    model[key + '__' + refKey] = value[refKey];
                });
                delete model[key];
                expanded = true;
            }
        });
        return expanded;
    }
});

window.tnx = tnxvue;

export default tnxvue;
