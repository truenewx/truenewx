// tnxcore-app.js

import util from './tnxcore-util';
import rpc from './tnxcore-app-rpc';

const app = {
    context: '',
    version: undefined,
    rpc: rpc,
    page: {
        context: "/pages",
        init(page, container) {
            if (typeof page === 'function') {
                page(container);
            } else { // 如果页面js组件不是初始化方法，则必须包含onLoad()方法，没有则报错
                page.onLoad(container);
            }
        }
    },
    _bindResourceLoad(element, url, onLoad) {
        if (typeof onLoad === 'function') {
            if (element.readyState) {
                element.onreadystatechange = function() {
                    if (element.readyState === 'loaded' || element.readyState === 'complete') {
                        element.onreadystatechange = null;
                        onLoad(url);
                    }
                }
            } else {
                element.onload = function() {
                    onLoad(url);
                }
            }
        }
    },
    _loadLink(url, container, callback) {
        const link = document.createElement('link');
        link.type = 'text/css';
        link.rel = 'stylesheet';
        this._bindResourceLoad(link, url, callback);
        link.href = url;

        const node = container.getFirstChildWithoutTagName('link');
        if (node) {
            container.insertBefore(link, node);
        } else {
            container.appendChild(link);
        }
    },
    _loadScript(url, container, callback) {
        const _this = this;
        if (typeof requirejs === 'function') {
            requirejs([url], function(page) {
                callback(url);
                _this.page.init(page, container);
            });
        } else {
            const script = document.createElement('script');
            script.type = 'text/javascript';
            this._bindResourceLoad(script, url, callback);
            script.src = url;
            container.appendChild(script);
        }
    },
    _loadedResources: {}, // 保存加载中和加载完成的资源
    _loadResources(resourceType, container, loadOneFunction, callback, recursive) {
        if (typeof container === 'function') {
            recursive = callback;
            callback = loadOneFunction;
            loadOneFunction = container;
            container = undefined;
        }
        container = container || document.body;

        const _this = this;
        let resources = container.getAttribute(resourceType);
        const children = container.querySelectorAll('[' + resourceType + ']');
        // 如果存在子容器需要加载，则当前容器不再加载
        if (children.length && recursive !== false) {
            children.forEach(function(child) {
                _this._loadResources(resourceType, child, loadOneFunction, callback, false);
            });
            if (resources) {
                console.warn(resources + ' is ignored.');
            }
        } else {
            let empty = true;
            if (resources) {
                resources = resources.split(',');
                resources.forEach(function(resource, i) {
                    resource = resource.trim();
                    const url = container.getAttribute('url');
                    let action = _this.getAction(url);
                    if (resource === 'true' || resource === 'default') {
                        resource = _this.page.context + action + '.' + resourceType;
                    }
                    if (resource.toLowerCase().endsWith('.' + resourceType)) {
                        // 不包含协议的为相对路径，才需要做路径转换
                        if (resource.indexOf('://') < 0) {
                            if (resource.startsWith('/')) { //以斜杠开头的为相对于站点根路径的相对路径
                                resources[i] = _this.context + resource;
                            } else { // 否则为相对于当前目录的相对路径
                                const index = action.lastIndexOf('/');
                                if (index >= 0) {
                                    action = action.substr(0, index);
                                }
                                resources[i] = _this.context + _this.page.context + action + '/' + resource;
                            }
                        }
                        if (_this.version) { // 脚本路径附加应用版本信息，以更新客户端缓存
                            resources[i] += '?v=' + _this.version;
                        }
                        _this._loadedResources[resource] = false;
                    } else { // 无效的脚本文件置空
                        resources[i] = undefined;
                    }
                });

                resources.forEach(function(resource) {
                    if (resource) {
                        empty = false;
                        loadOneFunction.call(_this, resource, container, function(url) {
                            _this._loadedResources[url] = true;
                            if (typeof callback === 'function' && _this._isAllLoaded(resources)) {
                                callback.call(_this);
                            }
                        });
                    }
                });
            }
            if (empty && typeof callback === 'function') {
                callback.call(this);
            }
        }
    },
    _isAllLoaded(resources) {
        const _this = this;
        for (let i = 0; i < resources.length; i++) {
            const resource = resources[i];
            if (_this._loadedResources[resource] !== true) {
                return false;
            }
        }
        return true;
    },
    _loadLinks(container, callback) {
        if (typeof container === 'function') {
            callback = container;
            container = undefined;
        }
        this._loadResources('css', container, this._loadLink, callback);
    },
    _loadScripts(container, callback) {
        if (typeof container === 'function') {
            callback = container;
            container = undefined;
        }
        this._loadResources('js', container, this._loadScript, callback);
    },
    getAction(url) {
        let href = url || window.location.href;
        // 去掉参数
        let index = href.indexOf('?');
        if (index >= 0) {
            href = href.substr(0, index);
        }
        // 去掉锚点
        index = href.indexOf('#');
        if (index >= 0) {
            href = href.substr(0, index);
        }
        // 去掉协议
        if (href.startsWith('//')) {
            href = href.substr(2);
        } else {
            index = href.indexOf('://');
            if (index >= 0) {
                href = href.substr(index + 3);
            }
        }
        // 去掉域名和端口
        index = href.indexOf('/');
        if (index >= 0) {
            href = href.substr(index);
        }
        // 去掉contextPath
        if (this.context !== '' && this.context !== '/' && href.startsWith(this.context)) {
            href = href.substr(this.context.length);
        }
        // 去掉后缀
        index = href.lastIndexOf('.');
        if (index >= 0) {
            href = href.substr(0, index);
        }
        if (href.endsWith('/')) {
            href = href.substr(0, href.length - 1);
        }
        return href;
    },
    init(container, callback) {
        // 初始化app环境
        const context = util.dom.getMetaContent('app.context');
        if (context) {
            this.context = context;
        }
        this.version = util.dom.getMetaContent('app.version');

        if (typeof container === 'function') {
            callback = container;
            container = undefined;
        }
        const _this = this;
        this._loadLinks(container, function() {
            _this._loadScripts(container, function() {
                if (typeof callback === 'function') {
                    callback.call();
                }
            });
        });
    },
    buildCsrfField(form) {
        const meta = document.querySelector('meta[name="csrf"]');
        if (meta) {
            const name = meta.getAttribute('parameter');
            const value = meta.getAttribute('content');
            if (name && value) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = name;
                input.value = value;
                form.appendChild(input);
            }
        }
    },
};

export default app;
