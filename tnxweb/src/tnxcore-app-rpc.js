// tnxcore-app-rpc.js
import {util} from './tnxcore-util';
import Axios from 'axios';

Axios.defaults.baseURL = '';
Axios.defaults.withCredentials = true; // 允许携带Cookie
const ajaxHeader = {'X-Requested-With': 'XMLHttpRequest'};
Object.assign(Axios.defaults.headers.common, ajaxHeader); // 标记为AJAX请求

function createClient(app) {
    return {
        baseUrl: app.baseUrl,
        request: Axios.create({
            baseURL: app.baseUrl,
            withCredentials: true,
            headers: ajaxHeader,
        }),
    };
}

export default {
    defaultClient: {
        baseUrl: Axios.defaults.baseURL,
        request: Axios,
    },
    appClients: {},
    loginSuccessRedirectParameter: '_next',
    logoutProcessUrl: '/logout',
    getDefaultBaseUrl() {
        return this.defaultClient.baseUrl;
    },
    getBaseUrl(appName) {
        let appClient = this.appClients[appName];
        return appClient ? appClient.baseUrl : undefined;
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
        if (baseUrl !== this.getDefaultBaseUrl()) {
            Axios.defaults.baseURL = baseUrl;
            this.defaultClient = createClient({baseUrl});
        }

        let _this = this;
        this.get('/api/meta/context', function(context) {
            _this.setConfig(context);
            if (typeof callback === 'function') {
                callback(context);
            }
        });
    },
    setConfig(config) {
        Object.assign(Axios.defaults.headers.common, config.headers);
        if (config.loginSuccessRedirectParameter) {
            this.loginSuccessRedirectParameter = config.loginSuccessRedirectParameter;
        }
        if (config.apps) {
            let appNames = Object.keys(config.apps);
            for (let appName of appNames) {
                let app = config.apps[appName];
                if (appName === config.baseApp) {
                    if (app.baseUrl !== this.getDefaultBaseUrl()) {
                        this.defaultClient = createClient(app);
                    }
                    this.appClients[appName] = this.defaultClient;
                } else {
                    this.appClients[appName] = createClient(app);
                }
                if (app.subs) {
                    let refClient = this.appClients[appName];
                    let subAppNames = Object.keys(app.subs);
                    for (let subAppName of subAppNames) {
                        this.appClients[subAppName] = {
                            ref: refClient,
                            contextUrl: app.subs[subAppName],
                        }
                    }
                }
            }
        }
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
        const config = {
            headers: {
                'Original-Page': window.location.href, // 用于登录失效时，跳转到登录页重新登录后，返回当前所在页
            },
            referer: url,
            method: options.method,
            params: options.params,
            data: options.body,
        };
        if (config.params) {
            config.paramsSerializer = function(params) {
                return util.net.toParameterString(params);
            };
        }
        if (config.data) {
            let keys = Object.keys(config.data);
            for (let key of keys) {
                let value = config.data[key];
                if (value instanceof Date) {
                    config.data[key] = value.formatDateTime();
                }
            }
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
        let client = options.app ? this.appClients[options.app] : null;
        if (!url.startsWith('/')) { // 绝对地址需找到对应的应用客户端，并转换为相对地址
            let appNames = Object.keys(this.appClients);
            for (let appName of appNames) {
                let appClient = this.appClients[appName];
                if (url.startsWith(appClient.baseUrl)) {
                    url = url.substr(appClient.baseUrl.length);
                    client = appClient;
                    break;
                }
            }
            if (!url.startsWith('/')) { // 无法转换为相对地址的一律使用全局客户端
                client = {
                    request: Axios
                }
            }
        }
        client = client || this.defaultClient;
        if (client.ref) {
            // 登录凭证验证请求直接向引用的所属应用发送，其它请求才需要添加上下文路径前缀
            if (!url.startsWith(this.authenticationContextUrl + '/')) {
                url = client.contextUrl + url;
            }
            client = client.ref;
        }
        let _this = this;
        client.request(url, config).then(function(response) {
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
                        const originalRequest = util.net.getHeader(response.headers, 'Original-Request');
                        let originalMethod;
                        let originalUrl;
                        if (originalRequest) {
                            const array = originalRequest.split(' ');
                            originalMethod = array[0];
                            originalUrl = array[1];
                        }
                        let loginUrl = util.net.getHeader(response.headers, 'Login-Url');
                        if (loginUrl) {
                            // 默认登录后跳转回当前页面，如果已指定跳转目标地址，则忽略
                            if (!loginUrl.contains('?' + _this.loginSuccessRedirectParameter + '=')
                                && !loginUrl.contains('&' + _this.loginSuccessRedirectParameter + '=')) {
                                let loginSuccessRedirectUrl = encodeURIComponent(window.location.href);
                                if (loginUrl.contains('?')) {
                                    loginUrl += '&';
                                } else {
                                    loginUrl += '?';
                                }
                                loginUrl += _this.loginSuccessRedirectParameter + '=' + loginSuccessRedirectUrl;
                            }
                        }
                        // 原始地址是授权验证地址或登出地址，视为框架特有请求，无需应用做个性化处理
                        if (originalUrl && (originalUrl.startsWith(_this.authenticationContextUrl + '/')
                            || originalUrl === _this.logoutProcessUrl)) {
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
                        if (response.data === '') { // 服务端已修正无操作权限时不正常报错的问题，此处暂留以待观察
                            response.data = {
                                errors: [{
                                    code: 'error.web.security.no_operation_authority',
                                    message: '没有权限访问 ' + url,
                                }]
                            };
                        }
                        if (_this.handleErrors(response.data.errors, options)) {
                            return;
                        }
                        break;
                    }
                    case 500: {
                        _this.handle500Error(response.data.message, options);
                        break;
                    }
                    default: {
                        _this.handleOtherError(url + ':\n' + error.stack, options);
                    }
                }
            }
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
    handleOtherError(message, options) {
        if (typeof options?.error === 'function') {
            options.error(message);
        } else {
            console.error(message);
        }
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
    /**
     * 登录凭证验证地址上下文前缀
     */
    authenticationContextUrl: '/authentication',
    isLogined(callback, options) {
        this.get(this.authenticationContextUrl + '/authorized', callback, options);
    },
    /**
     * 确保已登录
     * @param callback 校验通过的回调
     * @param options 请求选项集
     */
    ensureLogined(callback, options) {
        this.get(this.authenticationContextUrl + '/validate', callback, options);
    },
    /**
     * 确保已具有指定授权
     * @param authority 授权：{type,rank,permission}
     * @param callback 校验通过时的回调
     * @param options 请求选项集
     */
    ensureGranted(authority, callback, options) {
        this.get(this.authenticationContextUrl + '/validate', authority, callback, options);
    },
    getLoginUrl(callback, options) {
        this.get(this.authenticationContextUrl + '/login-url', callback, options);
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
    resolveEnumItem(type, subtype, key, callback) {
        if (typeof key === 'function') {
            callback = key;
            key = subtype;
            subtype = undefined;
        }
        if (key === undefined || key === null) {
            callback(null);
        } else {
            const mappingKey = subtype ? (type + '-' + subtype) : type;
            let items = this._enumItemsMapping[mappingKey];
            let item = this._getEnumItem(items, key);
            if (item) {
                callback(item);
            } else {
                let _this = this;
                this.loadEnumItems(type, subtype, function(items) {
                    let item = _this._getEnumItem(items, key);
                    if (item) {
                        callback(item);
                    }
                });
            }
        }
    },
    _getEnumItem(items, key) {
        if (Array.isArray(items)) {
            for (let item of items) {
                if (item.key === key) {
                    return item;
                }
            }
        }
        return undefined;
    },
    resolveEnumCaption(type, subtype, key, callback) {
        if (typeof key === 'function') {
            callback = key;
            key = subtype;
            subtype = undefined;
        }
        this.resolveEnumItem(type, subtype, key, function(item) {
            let caption = item ? item.caption : '';
            callback(caption);
        });
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
    },
    logout() {
        this.post(this.logoutProcessUrl);
    },
};
