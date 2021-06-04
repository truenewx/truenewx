// tnxcore-app-rpc.js
import {util} from "./tnxcore-util";
import axios from "axios";

export default {
    loginSuccessRedirectParameter: '_next',
    baseApp: undefined,
    apps: {},
    setBaseUrl(baseUrl) {
        axios.defaults.baseURL = baseUrl || '';
    },
    getBaseUrl() {
        return axios.defaults.baseURL;
    },
    /**
     * 从后端服务器加载配置
     * @param baseUrl 获取配置的后端服务器基础路径
     * @param callback 配置初始化后的回调函数
     */
    loadConfig(baseUrl, callback) {
        if (typeof baseUrl === 'function') {
            callback = baseUrl;
            baseUrl = undefined;
        }
        this.setBaseUrl(baseUrl);

        const _this = this;
        this.get('/api/meta/context', function(context) {
            _this.setConfig(context);
            if (typeof callback === 'function') {
                callback(context);
            }
        });
    },
    setConfig(config) {
        this.baseApp = config.baseApp;
        if (config.apps) { // 相关应用的上下文根路径
            this.apps = config.apps;
        }
        if (this.baseApp && this.apps) {
            axios.defaults.baseURL = this.apps[this.baseApp];
        }
        if (config.loginSuccessRedirectParameter) {
            this.loginSuccessRedirectParameter = config.loginSuccessRedirectParameter;
        }
        // 声明为AJAX请求
        Object.assign(axios.defaults.headers.common, config.headers, {
            'X-Requested-With': 'XMLHttpRequest'
        });
        axios.defaults.withCredentials = true;
    },
    get(url, params, callback, options) {
        if (typeof params === 'function' || (callback && typeof callback === 'object')) {
            options = callback;
            callback = params;
            params = undefined;
        }
        if (typeof options === 'function') {
            options = {
                error: options
            };
        }
        this.request(url, Object.assign({}, options, {
            method: 'get',
            params: params,
            success: callback,
        }));
    },
    post(url, body, callback, options) {
        if (typeof body === 'function' || (callback && typeof callback === 'object')) {
            options = callback;
            callback = body;
            body = undefined;
        }
        if (typeof options === 'function') {
            options = {
                error: options
            };
        }
        options = Object.assign({}, options, {
            method: 'post',
            body: body,
            success: callback,
        });
        this.request(url, options);
    },
    request(url, options) {
        if (options.app && url.startsWith('/')) {
            const appBaseUrl = this.apps[options.app];
            if (appBaseUrl) {
                url = appBaseUrl + url;
            }
        }
        const config = {
            referer: url,
            method: options.method,
            params: options.params,
            data: options.body,
        };
        if (config.params) {
            Object.keys(config.params).forEach(key => {
                let value = config.params[key];
                if (value === undefined || value === null) {
                    delete config.params[key];
                } else {
                    config.params[key] = value + ''; // 参数值都转换为字符串，以避免参数传递错误
                }
            });
            config.paramsSerializer = function(params) {
                return util.net.toParameterString(params);
            };
        }
        if (typeof options.onUploadProgress === 'function') {
            config.onUploadProgress = function(event) {
                const ratio = (event.loaded / event.total) || 0;
                options.onUploadProgress.call(event, ratio);
            }
        }
        this._request(url, config, options);
    },
    _request(url, config, options) {
        const _this = this;
        axios(url, config).then(function(response) {
            if (_this._redirectRequest(response, config, options)) { // 执行了重定向跳转，则不作后续处理
                return;
            }
            if (typeof options.success === 'function') {
                options.success(response.data);
            }
        }).catch(function(error) {
            const response = error.response;
            if (response) {
                if (_this._isIgnored(options, response.status)) {
                    return;
                }
                if (_this._redirectRequest(response, config, options)) { // 执行了重定向跳转，则不作后续处理
                    return;
                }
                switch (response.status) {
                    case 401: {
                        let loginUrl = util.net.getHeader(response.headers, 'Login-Url');
                        if (loginUrl) {
                            // 默认登录后跳转回当前页面
                            if (loginUrl.contains('?')) {
                                loginUrl += '&';
                            } else {
                                loginUrl += '?';
                            }
                            const loginSuccessRedirectUrl = encodeURIComponent(window.location.href);
                            loginUrl += _this.loginSuccessRedirectParameter + '=' + loginSuccessRedirectUrl;
                        }
                        const originalRequest = util.net.getHeader(response.headers, 'Original-Request');
                        let originalMethod;
                        let originalUrl;
                        if (originalRequest) {
                            const array = originalRequest.split(' ');
                            originalMethod = array[0];
                            originalUrl = array[1];
                        }
                        // 原始地址是登录验证地址或登出地址，视为框架特有请求，无需应用做个性化处理
                        if (originalUrl && (originalUrl === _this._ensureLoginedUrl || originalUrl.endsWith(
                            '/logout'))) {
                            originalUrl = undefined;
                            originalMethod = undefined;
                        }
                        const toLogin = options.toLogin || _this.toLogin;
                        if (toLogin(loginUrl, originalUrl, originalMethod)) {
                            return;
                        }
                        break;
                    }
                    case 400: {
                        let errors = response.data.errors;
                        if (errors && errors.length) { // 字段格式异常
                            errors.forEach(error => {
                                if (!error.message && error.defaultMessage) {
                                    error.message = error.field + ' ' + error.defaultMessage;
                                }
                            });
                            // 转换错误消息之后，与403错误做相同处理
                            if (_this.handleErrors(errors, options)) {
                                return;
                            }
                        } else if (response.data.message) {
                            console.error(response.data.message);
                            return;
                        }
                        break;
                    }
                    case 403: {
                        if (_this.handleErrors(response.data.errors, options)) {
                            return;
                        }
                        break;
                    }
                    case 500: {
                        _this.handle500Error(response.data.message, options);
                        break;
                    }
                }
            }
            console.error(url + ':\n' + error.stack);
        });
    },
    _redirectRequest(response, config, options) {
        let redirectUrl = util.net.getHeader(response.headers, 'Redirect-To');
        if (redirectUrl) { // 指定了重定向地址，则执行重定向操作
            if (this._isIgnored(options, 'Redirect-To')) {
                return true;
            }
            config.headers = config.headers || {};
            config.headers['Original-Request'] = options.method + ' ' + config.referer;
            config.method = 'GET'; // 重定向一定是GET请求
            this._request(redirectUrl, config, options);
            return true;
        }
        return false;
    },
    _isIgnored(options, type) {
        if (options && options.ignored) {
            if (options.ignored instanceof Array) {
                return options.ignored.contains(type);
            } else {
                return options.ignored === type;
            }
        }
        return false;
    },
    /**
     * 打开登录表单的函数，由业务应用覆盖提供，以决定用何种方式打开登录表单页面。
     * 默认不做任何处理，直接返回false
     * @param loginFormUrl 登录表单URL
     * @param originalUrl 原始请求地址
     * @param originalMethod 原始请求方法
     * @returns {boolean} 是否已经正常打开登录表单
     */
    toLogin(loginFormUrl, originalUrl, originalMethod) {
        return false;
    },
    handle500Error(message, options) {
        console.error(message);
        this.handleErrors([{
            message: '哎呀，非常抱歉服务器出了点小小的错误，这并不影响你的其它操作，我们会尽快修正这个错误。'
        }], options);
    },
    handleErrors(errors, options) {
        if (errors) {
            if (options && typeof options.error === 'function') {
                options.error(errors);
            } else {
                this.error(errors);
            }
            return true;
        }
        return false;
    },
    error(errors) {
        const message = this.getErrorMessage(errors);
        window.tnx.error(message);
    },
    getErrorMessage(errors) {
        let message = '';
        if (!Array.isArray(errors)) {
            errors = [errors];
        }
        for (let i = 0; i < errors.length; i++) {
            message += errors[i].message + '\n';
        }
        return message.trim();
    },
    _ensureLoginedUrl: '/authentication/validate',
    ensureLogined(callback, options) {
        this.get(this._ensureLoginedUrl, callback, options);
    },
    _metas: {},
    getMeta(urlOrType, callback, app) {
        const metaKey = app ? (app + ':/' + urlOrType) : urlOrType;
        const metas = this._metas;
        if (metas[metaKey]) {
            if (typeof callback === 'function') {
                callback(metas[metaKey]);
            }
        } else {
            let url = '/api/meta/' + (urlOrType.contains('/') ? 'method' : 'model');
            let params = urlOrType.contains('/') ? {url: urlOrType} : {type: urlOrType};
            this.get(url, params, function(meta) {
                if (meta) {
                    metas[metaKey] = meta;
                    if (typeof callback === 'function') {
                        callback(meta);
                    }
                }
            }, {app});
        }
    },
    _enumItemsMapping: {},
    loadEnumItems(type, subtype, callback) {
        if (typeof subtype === 'function') {
            callback = subtype;
            subtype = undefined;
        }
        const mapping = this._enumItemsMapping;
        const mappingKey = subtype ? (type + '-' + subtype) : type;
        if (mapping[mappingKey]) {
            if (typeof callback === 'function') {
                callback(mapping[mappingKey]);
            }
        } else {
            this.get('/api/meta/enums', {
                type, subtype
            }, function(items) {
                mapping[mappingKey] = items;
                if (typeof callback === 'function') {
                    callback(items);
                }
            });
        }
    },
    resolveEnumCaption(type, subtype, key, callback) {
        if (typeof key === 'function') {
            callback = key;
            key = subtype;
            subtype = undefined;
        }
        if (key === undefined || key === null) {
            callback('');
        } else {
            const mappingKey = subtype ? (type + '-' + subtype) : type;
            let items = this._enumItemsMapping[mappingKey];
            let caption = this._getEnumCaption(items, key);
            if (caption) {
                callback(caption);
            } else {
                let _this = this;
                this.loadEnumItems(type, function(items) {
                    callback(_this._getEnumCaption(items, key));
                });
            }
        }
    },
    _getEnumCaption(items, key) {
        if (Array.isArray(items)) {
            for (let item of items) {
                if (item.key === key) {
                    return item.caption;
                }
            }
        }
        return undefined;
    },
    _regionMapping: {},
    loadRegion(regionCode, level, callback) {
        if (typeof level === 'function') {
            callback = level;
            level = undefined;
        }
        level = Math.min(level || 3, 3);
        let cacheKey = regionCode + '.' + level;
        let region = this._regionMapping[cacheKey];
        if (region) {
            callback(region);
        } else {
            let _this = this;
            this.get('/api/region/' + regionCode, function(region) {
                _this._filterRegionSubs(region, level);
                _this._regionMapping[cacheKey] = region;
                callback(region);
            });
        }
    },
    _filterRegionSubs(region, level) {
        if (region.subs) {
            if (region.level >= level) {
                delete region.subs;
            } else {
                for (let sub of region.subs) {
                    this._filterRegionSubs(sub, level);
                }
            }
        }
    }
};
