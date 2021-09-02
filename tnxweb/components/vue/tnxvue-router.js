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
                isHistory() {
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

    const router = new VueRouter({routes});

    // 监听浏览器的返回事件
    if (window.history && window.history.pushState) {
        window.history.pushState(null, null, document.URL);
        window.addEventListener('popstate', function() {
            // 此函数先于VueRouter的所有钩子执行
            router._historied = true;
        }, false);
    }

    // 注册离开页面前事件处理支持
    router.$beforeLeaveHandlers = {};
    router.beforeLeave = function(handler) {
        if (typeof handler === 'function') {
            let path = router.app.$route.path;
            router.$beforeLeaveHandlers[path] = handler;
        }
    };
    router.beforeEach((to, from, next) => {
        window.tnx.app.page.stopCache(router, from.path);

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
    router.afterEach((to, from) => {
        let $route = router.app.$route;
        if (router._historied) {
            delete router._historied;
            if ($route) {
                $route.meta.historyFrom = from.path;
            }
        } else if ($route) {
            delete $route.meta.historyFrom;
        }
        // 前后路径相同，但全路径不同（意味着参数不同），则需要刷新页面，否则页面不会刷新
        if (to.path === from.path && to.fullPath !== from.fullPath) {
            window.location.reload();
        } else {
            router.prev = from;
        }
    });
    router.back = FunctionUtil.around(router.back, function(back, path) {
        let route = router.app.$route;
        // 如果上一页路径为指定路径，或匹配上级菜单路径，则执行原始的返回
        if (router.prev) {
            if (router.prev.path === path || matchesPath(path || router.prev.path, route.meta.superiorPath)) {
                back.call(router);
                return;
            }
        }
        // 如果没有上一页路径，或者上一页路径为根目录，则替换到上级菜单页面
        if (!router.prev || router.prev.path === '/') {
            path = path || route.meta.superiorPath;
        }
        path = instantiatePath(path, route.params);
        if (path) {
            router._historied = true; // 标记路由器正在执行历史动作
            router.replace(path);
            return;
        }
        // 直接返回上一页作为兜底
        back.call(router);
    });
    return router;
}
