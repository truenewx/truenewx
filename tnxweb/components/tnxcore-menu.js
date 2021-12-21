/**
 * 菜单组件
 * 菜单配置中的权限配置不是服务端权限判断的依据，仅用于生成具有权限的客户端菜单，以及分配权限时展示可分配的权限范围
 */
function isGranted(authorities, item) {
    prepareItem(item);

    if (item.rank || item.permission) {
        for (let authority of authorities) {
            // 菜单不限定级别视为级别匹配；已获级别权限为全部视为级别匹配；已获级别等于菜单限定级别视为级别匹配
            let rankMatched = !item.rank || authority.rank === '*' || authority.rank === item.rank;
            if (rankMatched) {
                // 级别匹配，还需进一步比较许可
                if (item.permission) {
                    // 权限许可集包含全部许可，或菜单项许可，则视为结果匹配
                    if (authority.permissions && (authority.permissions.contains('*')
                        || authority.permissions.containsIgnoreCase(item.permission))) {
                        return true;
                    }
                } else { // 级别匹配，且未限定许可，则视为结果匹配
                    return true;
                }
            }
            // 级别不匹配，则检查下一条权限
        }
        return false;
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
    let permission = path.replace(/\/:[^\/]+\//g, '/').replace(/\*/g, '');
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

function applyGrantedItemToItems(authorities, item, items) {
    const granted = isGranted(authorities, item);
    if (granted === true) { // 授权匹配
        items.push(Object.assign({}, item));
    } else if (granted === false) { // 授权不匹配
        // 不做处理
    } else { // 无法判断，需到子节点中查找
        if (item.subs && item.subs.length) {
            const subs = [];
            for (let sub of item.subs) {
                if (isGranted(authorities, sub)) {
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
    if (item.path && matchesPath(item.path, path)) {
        return true;
    }
    if (typeof item.permission === 'string') { // 如果没有指定路径但指定了许可名
        if (item.permittedPath) { // 如果指定了许可路径，则先尝试匹配许可路径和指定路径
            if (!Array.isArray(item.permittedPath)) {
                item.permittedPath = [item.permittedPath];
            }
            for (let permittedPath of item.permittedPath) {
                if (matchesPath(permittedPath, path)) {
                    return true;
                }
            }
        }
        // 此时还未匹配，则将指定路径按照默认规则转换为许可名尝试匹配
        let permission = getDefaultPermission(path);
        return item.permission === permission;
    }
    return false;
}

function matchesPath(pathPattern, actualPath) {
    let pattern = pathPattern.replace(/\/:[a-zA-Z0-9_]+/g, '/[a-zA-Z0-9_\\*]+');
    if (pattern === pathPattern) { // 无路径参数
        return pathPattern === actualPath;
    } else { // 有路径参数
        return new RegExp(pattern, 'g').test(actualPath);
    }
}

function buildLevel(items, parentLevel) {
    if (items) {
        let level = parentLevel ? (parentLevel + 1) : 1;
        for (let item of items) {
            item.level = level;
            item.isSubVisible = function() {
                if (item.subVisible === false) {
                    return false;
                }
                if (item.subs && item.subs.length) {
                    let visible = true;
                    for (let sub of item.subs) {
                        visible = (sub.visible !== false) && visible;
                    }
                    return visible;
                }
                return false;
            };
            buildLevel(item.subs, level);
        }
    }
    return items;
}

const Menu = function Menu(config) {
    this.app = config.app;
    this.caption = config.caption;
    this.items = buildLevel(config.items);
    this.scope = config.scope;
    this._url = config.url;
    this._grantedItems = null;
    this.authorities = [];
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
                return;
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
        if (item.level <= level && item.visible !== false) {
            let parentItem = items[i - 1];
            if (!parentItem || parentItem.isSubVisible()) {
                return item;
            }
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
    let breadcrumbItems = this.getBreadcrumbItems(path);
    if (breadcrumbItems.length) {
        for (let i = breadcrumbItems.length - 1; i >= 0; i--) {
            let item = breadcrumbItems[i];
            if (item.rank || item.permission) {
                return isGranted(this.authorities, item);
            }
        }
        return true; // 有匹配菜单项，但各级菜单项均未设置可鉴权，则说明无需鉴权，视为鉴权通过
    }
    return false;
};

Menu.prototype.loadGrantedItems = function(scope, callback) {
    if (typeof scope === 'function') {
        callback = scope;
        scope = null;
    }
    if (scope && scope !== this.scope) {
        this._grantedItems = null;
    }
    if (this._grantedItems) {
        callback(this._grantedItems);
    } else {
        const _this = this;
        if (this._url) {
            window.tnx.app.rpc.get(this._url, function(authorities) {
                _this._applyAuthorities(authorities, scope, callback);
            });
        } else { // 未指定获权地址，则视为具有所有权限，用于没有或不关心服务端权限的场景
            this._applyAuthorities({
                rank: '*',
                permissions: ['*'],
            }, scope, callback);
        }
    }
}

Menu.prototype._applyAuthorities = function(authorities, scope, callback) {
    if (!Array.isArray(authorities)) {
        authorities = [authorities];
    }
    this.authorities = authorities;
    this.scope = scope;

    this._grantedItems = [];
    for (let item of this.items) {
        applyGrantedItemToItems(this.authorities, item, this._grantedItems);
    }
    callback(this._grantedItems);
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
