/**
 * 基于Vue的路由器构建函数
 */
import {FunctionUtil} from '../tnxcore-util';

function addRoute(routes, superiorPath, item, fnImportPage) {
    if (item && item.path) {
        routes.push({
            path: item.path,
            meta: {
                superiorPath: superiorPath,
                cache: {}, // 路由级缓存
                isHistory() { // 通过setTimeout()方式调用才能确保获得正确结果
                    return this.historyFrom !== undefined;
                }
            },
            component() {
                let path = item.page || item.path.replace(/\/:[a-zA-Z0-9_]+/g, '');
                return fnImportPage(path);
            },
        });
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
    if (window.history && window.history.pushState) {
        window.history.pushState(null, null, document.URL);
        window.addEventListener('popstate', function() {
            let $route = getCurrentRoute(router);
            if ($route) {
                $route.meta.historyFrom = router.history.state.forward;
            }
        }, false);
    }

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
        // 前后路径相同，但全路径不同（意味着参数不同），则需要刷新页面，否则页面不会刷新
        if (to.path === from.path && to.fullPath !== from.fullPath) {
            window.location.reload();
        }
    });
    router.back = FunctionUtil.around(router.back, function(back, path) {
        let prevPath = router.history.state.back;
        let $route = getCurrentRoute(router);
        // 如果上一页路径为指定路径，或匹配上级菜单路径，则执行原始的返回
        if (prevPath === path || matchesPath(path || prevPath, $route.meta.superiorPath)) {
            back.call(router);
            return;
        }
        // 如果没有上一页路径，则替换到上级菜单页面
        if (!prevPath) {
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
    return router;
}
