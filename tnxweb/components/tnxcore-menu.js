/**
 * 菜单组件
 * 菜单配置中的权限配置不是服务端权限判断的依据，仅用于生成具有权限的客户端菜单，以及分配权限时展示可分配的权限范围
 */
function isGranted(authority, item) {
    prepareItem(item);
    if (item.rank || item.permission) {
        if (item.rank) {
            if (authority.rank !== item.rank) {
                return false;
            }
        }
        if (item.permission) {
            if (authority.permissions.contains('*')) {
                return true;
            }
            if (!authority.permissions.containsIgnoreCase(item.permission)) {
                return false;
            }
        }
    } else if (item.subs && item.subs.length) {
        return undefined;
    }
    return true;
}

function prepareItem(item) {
    if (item.path && item.permission === true) {
        item.permission = getDefaultPermission(item.path);
    }
}

function getDefaultPermission(path) {
    // 确保路径头尾都有/
    if (!path.startsWith('/')) {
        path = '/' + path;
    }
    if (!path.endsWith('/')) {
        path += '/';
    }
    // 移除可能包含的路径变量
    let permission = path.replace(/\/:[^\/]+\//g, '/');
    // 去掉许可头尾的/
    if (permission.startsWith('/')) {
        permission = permission.substr(1);
    }
    if (permission.endsWith('/')) {
        permission = permission.substr(0, permission.length - 1);
    }
    // 许可所有中间的/替换为_
    permission = permission.replace(/\//g, '_');
    return permission;
}

function applyGrantedItemToItems(authority, item, items) {
    const granted = isGranted(authority, item);
    if (granted === true) { // 授权匹配
        items.push(Object.assign({}, item));
    } else if (granted === false) { // 授权不匹配
        // 不做处理
    } else { // 无法判断，需到子节点中查找
        if (item.subs && item.subs.length) {
            const subs = [];
            for (let sub of item.subs) {
                if (isGranted(authority, sub)) {
                    subs.push(Object.assign({}, sub));
                }
            }
            if (subs.length) {
                items.push(Object.assign({}, item, {
                    subs: subs
                }));
            }
        }
    }
}

function findItem(path, items, callback) {
    if (path && items && items.length && typeof callback === 'function') {
        for (let item of items) {
            if (matches(item, path)) {
                return callback(item);
            }
            // 直接路径不匹配，则尝试在子菜单中查找
            if (item.subs) {
                let result = findItem(path, item.subs, callback);
                if (result) {
                    return callback(item, result);
                }
            }
        }
    }
    return undefined;
}

function matches(item, path) {
    // 去掉可能的请求参数部分
    const index = path.indexOf('?');
    if (index >= 0) {
        path = path.substr(index);
    }
    if (item.path) {
        let pattern = item.path.replace(/\/:[a-zA-Z0-9_]+/g, '/[a-zA-Z0-9_\\*]+');
        if (pattern === item.path) { // 无路径参数
            if (item.path === path) {
                return true;
            }
        } else { // 有路径参数
            if (new RegExp(pattern, 'g').test(path)) {
                return true;
            }
        }
    }
    return false;
}

function buildLevel(items, parentLevel) {
    if (items) {
        let level = parentLevel ? (parentLevel + 1) : 1;
        for (let item of items) {
            item.level = level;
            buildLevel(item.subs, level);
        }
    }
    return items;
}

const Menu = function Menu(config) {
    this.app = config.app;
    this.caption = config.caption;
    this.items = buildLevel(config.items);
    this._url = config.url;
    this._grantedItems = null;
    this.authority = {
        type: null,
        rank: null,
        app: null,
        permissions: [],
    };
}

Menu.prototype.getItemByPath = function(path) {
    return findItem(path, this.items, (item, sub) => {
        return sub || item;
    });
};

function addMatchedItemTo(items, path, targetItems) {
    if (items) {
        for (let item of items) {
            if (matches(item, path)) { // 找到匹配的菜单项，则加入目标项目清单，直接返回
                targetItems.push(item);
                return;
            }
            // 不匹配则尝试比较下级菜单项
            addMatchedItemTo(item.subs, path, targetItems);
            if (targetItems.length > 0) { // 如果在下级菜单中找到匹配，则当前级别需要插入到目标清单的首位
                targetItems.unshift(item);
            }
        }
    }
}

Menu.prototype.findBelongingItem = function(path, level) {
    level = level || 2;
    let items = [];
    addMatchedItemTo(this.items, path, items);
    // 从后往前遍历结果清单，以便于取到级别不高于目标级别的菜单项
    for (let i = items.length - 1; i >= 0; i--) {
        let item = items[i];
        if (item.level <= level) {
            return item;
        }
    }
    return undefined;
}

function findItemByPermission(items, permission) {
    for (let item of items) {
        prepareItem(item);
        if (item.permission === permission) {
            return item;
        }
        if (item.subs) {
            const sub = findItemByPermission(item.subs, permission);
            if (sub) {
                return sub;
            }
        }
    }
    return undefined;
}

Menu.prototype.getItemByPermission = function(permission) {
    return findItemByPermission(this.items, permission);
}

function findAssignableItems(items) {
    const assignableItems = [];
    items.forEach(item => {
        let assignableItem = {
            subs: [],
        };
        prepareItem(item);
        if (item.subs && item.subs.length) {
            assignableItem.subs = findAssignableItems(item.subs);
        }
        // 当前菜单有许可限定，或有可分配的子级或操作，则当前菜单项为可分配项
        if (item.permission || assignableItem.subs.length) {
            assignableItem = Object.assign({}, item, assignableItem);
            assignableItems.push(assignableItem);
        }
    });
    return assignableItems;
}

Menu.prototype.getAssignableItems = function() {
    return findAssignableItems(this.items);
}

Menu.prototype.getBreadcrumbItems = function(path) {
    let breadcrumbItems = findItem(path, this.items, (item, breadcrumbItems) => {
        if (breadcrumbItems && breadcrumbItems.length) {
            breadcrumbItems.unshift(item);
            return breadcrumbItems;
        } else {
            return [item];
        }
    });
    return breadcrumbItems || [];
};

Menu.prototype.isGranted = function(path) {
    const _this = this;
    return findItem(path, this.items, (item, sub) => {
        return isGranted(_this.authority, sub || item);
    });
};

Menu.prototype.loadGrantedItems = function(callback) {
    if (this._grantedItems) {
        callback(this._grantedItems);
    } else {
        const _this = this;
        window.tnx.app.rpc.get(this._url, function(authority) {
            if (authority && authority.length) {
                authority = authority[0];
            }
            authority = authority || {};
            authority.permissions = authority.permissions || [];
            _this.authority = authority;

            _this._grantedItems = [];
            _this.items.forEach(item => {
                applyGrantedItemToItems(_this.authority, item, _this._grantedItems);
            });
            callback(_this._grantedItems);
        });
    }
}

Menu.prototype.resolveItemPathParams = function(vm, item) {
    if (item && item.path) {
        let path = item.path;
        if (path.contains('/:')) { // 包含路径参数
            // 确保路径头尾都有/
            if (!path.startsWith('/')) {
                path = '/' + path;
            }
            if (!path.endsWith('/')) {
                path += '/';
            }
            // 替换路径参数
            let params = vm.$route.params;
            Object.keys(params).forEach(key => {
                path = path.replace('/:' + key + '/', '/' + params[key] + '/');
            });
            path = path.substr(0, path.length - 1); // 去掉末尾的/
        }
        return path;
    }
    return undefined;
}

export default Menu;
