/**
 * 基于Vue的路由器构建函数
 */
import {FunctionUtil, NetUtil} from '../tnxcore-util';

function addRoute(routes, superiorPath, item, fnImportPage) {
    if (item && item.path) {
        let page = item.page || item.path.replace(/\/:[a-zA-Z0-9_]+/g, '');
        let route = {
            path: item.path,
            meta: {
                superiorPath: superiorPath,
                page: page,
                cache: {}, // 路由级缓存
                isHistory() { // 通过setTimeout()方式调用才能确保获得正确结果
                    return this.historyFrom !== undefined;
                }
            },
            component() {
                return fnImportPage(page);
            },
        };
        // 如果直接定义route的redirect/alias字段，则item的redirect/alias为undefined时，route仍然有redirect/alias字段，只是其值为undefined，这将导致VueRouter报错
        if (item.redirect) {
            route.redirect = item.redirect;
        }
        if (item.alias) {
            route.alias = item.alias;
        }
        routes.push(route);
    }
}

function applyItemsToRoutes(superiorPath, items, routes, fnImportPage) {
    if (items && items.length) {
        items.forEach(item => {
            addRoute(routes, superiorPath, item, fnImportPage);
            applyItemsToRoutes(item.path, item.subs, routes, fnImportPage);
        });
    }
}

function matchesPath(path, pattern) {
    if (path === pattern) {
        return true;
    }
    if (pattern && pattern.contains('/:')) {
        pattern = pattern.replace(/\/:[a-zA-Z0-9_]+/g, '/\\w+');
        return new RegExp(pattern).test(path);
    }
    return false;
}

function instantiatePath(path, params) {
    if (path && path.contains('/:')) {
        if (params) {
            Object.keys(params).forEach(key => {
                path = path.replace('/:' + key + '/', '/' + params[key] + '/');
            });
        }
        if (path.contains('/:')) { // 参数替换完之后，还有路径参数，则为无效路径，返回首页
            console.warn('路径中的参数无法获得参数值，请确保具有参数的路径所属菜单项的下级菜单路径包含相同的参数：' + path);
            return '/';
        }
    }
    return path;
}

function getCurrentRoute(router) {
    return router.currentRoute._value;
}

export default function(VueRouter, menu, fnImportPage) {
    let items;
    if (Array.isArray(menu)) {
        items = [];
        menu.forEach(function(m) {
            items = items.concat(m.items);
        });
    } else {
        items = menu.items;
    }

    const routes = [];
    applyItemsToRoutes(undefined, items, routes, fnImportPage);

    const routerHistory = VueRouter.createHistory();
    const router = VueRouter.createRouter({
        history: routerHistory,
        routes,
    });
    router.history = routerHistory;

    // 浏览器的返回事件触发位于VueRouter的钩子执行和页面渲染之后，这意味着$route.meta.historyFrom必须在页面渲染完之后才具有正确的值
    // if (window.history && window.history.pushState) {
    //     window.history.pushState(null, null, document.URL);
    // }
    window.addEventListener('popstate', function() {
        let $route = getCurrentRoute(router);
        if ($route) {
            $route.meta.historyFrom = router.history.state.forward;
        }
    }, false);

    // 注册离开页面前事件处理支持
    router.$beforeLeaveHandlers = {};
    router.beforeLeave = function(handler) {
        if (typeof handler === 'function') {
            let $route = getCurrentRoute(router);
            let path = $route.path;
            router.$beforeLeaveHandlers[path] = handler;
        }
    };

    router.beforeEach(function(to, from, next) {
        if (typeof window.tnx.router.beforeLeave === 'function') {
            window.tnx.router.beforeLeave(router, from);
        }

        let allow = true;
        let beforeLeaveHandler = router.$beforeLeaveHandlers[from.path];
        if (beforeLeaveHandler) {
            if (beforeLeaveHandler(to) === false) {
                allow = false;
            }
        }
        if (allow) {
            next();
        }
    });

    router.afterEach(function(to, from) {
        router.prev = from;
        // 前后hash相同，但全路径不同（意味着参数不同），则需要刷新页面，否则页面不会刷新
        if (to.href === from.href && to.fullPath !== from.fullPath) {
            window.location.reload();
        }
    });

    router.back = FunctionUtil.around(router.back, function(back, path) {
        let $route = getCurrentRoute(router);
        // 如果上一页路径为指定路径，或匹配上级菜单路径，则执行原始的返回
        if (router.prev) {
            if (router.prev.path === path || matchesPath(path || router.prev.path, $route.meta.superiorPath)) {
                back.call(router);
                return;
            }
        }
        // 如果没有上一页路径，则替换到上级菜单页面。如果记录的上一页路径与history中的返回路径不一致，说明页面进行了刷新，无法进行简单返回
        if (!router.prev || !router.history.state.back || router.prev.path !== router.history.state.back) {
            path = path || $route.meta.superiorPath;
        }
        path = instantiatePath(path, $route.params);
        if (path) {
            router.replace(path);
            return;
        }
        // 直接返回上一页作为兜底
        back.call(router);
    });

    router.pushState = function(path) {
        let success = NetUtil.pushState('#' + path);
        if (!success) {
            this.push(path);
        }
        return success;
    }

    router.replaceState = function(path) {
        let success = NetUtil.replaceState('#' + path);
        if (!success) {
            this.replace(path);
        }
        return success;
    }

    return router;
}
