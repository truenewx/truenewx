<template>
    <el-tree class="permission-tree"
        ref="tree"
        :data="nodes"
        :default-expand-all="true"
        :expand-on-click-node="false"
        node-key="id"
        :style="{'max-height': maxHeight}"
    >
        <template #default="{node, data}">
            <div class="permission-node">
                <el-checkbox v-model="data.checked" v-if="data.permission" @change="()=>{
                    onCheckChange(data);
                }">{{ data.label }}
                </el-checkbox>
                <span v-else-if="node">{{ data.label }}</span>
                <span class="text-muted" :class="{'d-none': !data.remark}">({{ data.remark }})</span>
            </div>
        </template>
    </el-tree>
</template>

<script>
function isChecked(app, permissions, permission) {
    if (permission && permissions) {
        if (app) {
            permission = app + '.' + permission;
        }
        return permissions.contains(permission);
    }
    return false;
}

function addMenuItemToTreeNodes(app, parentId, menuItems, treeNodes, permissions) {
    for (let i = 0; i < menuItems.length; i++) {
        const item = menuItems[i];
        if (item.caption) { // 有显示名称才可进行权限分配，否则只是一个隐藏的菜单项
            let node = {
                id: (parentId || 'node') + '-' + i,
                parentId: parentId,
                label: item.caption,
                path: item.path,
                permission: item.permission,
                checked: isChecked(app, permissions, item.permission),
                remark: item.desc || item.permissionCaption,
            };
            if (item.subs && item.subs.length) {
                node.children = node.children || [];
                addMenuItemToTreeNodes(app, node.id, item.subs, node.children, permissions);
            }
            treeNodes.push(node);
        }
    }
}

function getTreeNodes(menu, permissions) {
    const nodes = [];
    if (menu) {
        let items = menu.getAssignableItems();
        addMenuItemToTreeNodes(menu.app, undefined, items, nodes, permissions);
    }
    return nodes;
}

function addCheckedNodePermissionTo(app, nodes, permissions) {
    for (let node of nodes) {
        if (node.checked && node.permission) {
            let permission = node.permission;
            if (app) {
                permission = app + '.' + permission;
            }
            permissions.push(permission);
        }
        if (node.children) {
            addCheckedNodePermissionTo(app, node.children, permissions);
        }
    }
}

function setCheckdByPermission(nodes, permission, checked) {
    if (nodes && permission) {
        for (let node of nodes) {
            if (node.permission === permission) {
                node.checked = checked;
            }
            setCheckdByPermission(node.children, permission, checked);
        }
    }
}

export default {
    name: 'TnxelPermissionTree',
    props: {
        menu: {
            type: Object,
            required: true,
        },
        permissions: Array,
        maxHeight: String,
    },
    data() {
        return {
            nodes: getTreeNodes(this.menu, this.permissions)
        };
    },
    watch: {
        menu(menu) {
            this.nodes = getTreeNodes(menu, this.permissions);
        },
        permissions(permissions) {
            this.nodes = getTreeNodes(this.menu, permissions);
        }
    },
    methods: {
        getNode(nodeId, nodes) {
            if (nodeId) {
                nodes = nodes || this.nodes;
                for (let node of nodes) {
                    if (node.id === nodeId) {
                        return node;
                    }
                    if (node.children) {
                        let child = this.getNode(nodeId, node.children);
                        if (child) {
                            return child;
                        }
                    }
                }
            }
            return undefined;
        },
        onCheckChange(node) {
            if (node.checked) { // 节点被选中，则上级节点必须选中
                let parentNode = this.getNode(node.parentId);
                if (parentNode) {
                    parentNode.checked = true;
                    setCheckdByPermission(this.nodes, parentNode.permission, true);
                }
            } else { // 节点未选中，则下级节点必须全部未选中
                if (node.children) {
                    node.children.forEach(child => {
                        child.checked = false;
                        setCheckdByPermission(this.nodes, child.permission, false);
                    });
                }
            }
            setCheckdByPermission(this.nodes, node.permission, node.checked);
        },
        getPermissions(withApp) {
            // 除非指定不附带app，否则默认根据菜单中配置的app进行附带
            let app = this.menu.app;
            if (withApp === false) {
                app = null;
            }

            const permissions = [];
            addCheckedNodePermissionTo(app, this.nodes, permissions);
            return permissions;
        }
    }
}
</script>

<style>
.permission-tree {
    overflow-y: auto;
    padding: 4px;
}

.permission-tree .permission-node {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.permission-tree .permission-node > :last-child {
    margin-left: 6px;
    margin-right: 6px;
    white-space: normal;
    line-height: 1;
    text-align: right;
}
</style>
