<template>
    <el-tree
        ref="tree"
        :data="nodes"
        :default-expand-all="true"
        :expand-on-click-node="false"
        node-key="id"
        class="px-1 py-2"
        :style="{'max-height': maxHeight}"
    >
        <div class="permission-node" slot-scope="{node, data}">
            <el-checkbox v-model="data.checked" v-if="data.permission" @change="()=>{
                onCheckChange(data);
            }">{{ data.label }}
            </el-checkbox>
            <span v-else-if="node">{{ data.label }}</span>
            <span class="text-muted" :class="{'d-none': !data.remark}">({{ data.remark }})</span>
        </div>
    </el-tree>
</template>

<script>
function addMenuItemToTreeNodes(parentId, menuItems, treeNodes, permissions) {
    for (let i = 0; i < menuItems.length; i++) {
        const item = menuItems[i];
        let node = {
            id: (parentId || 'node') + '-' + i,
            parentId: parentId,
            label: item.caption,
            path: item.path,
            permission: item.permission,
            checked: item.permission && permissions && permissions.contains(item.permission),
            remark: item.desc,
        };
        if (item.subs && item.subs.length) {
            node.children = node.children || [];
            addMenuItemToTreeNodes(node.id, item.subs, node.children, permissions);
        }
        treeNodes.push(node);
    }
}

function getTreeNodes(menu, permissions) {
    const nodes = [];
    if (menu) {
        let items = menu.getAssignableItems();
        addMenuItemToTreeNodes(undefined, items, nodes, permissions);
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
            let app = withApp ? this.menu.app : null;
            const permissions = [];
            addCheckedNodePermissionTo(app, this.nodes, permissions);
            return permissions;
        }
    }
}
</script>

<style scoped>
.permission-node {
    width: 100%;
}

.permission-node span:last-child {
    float: right;
    margin-right: 6px;
}

.el-tree {
    overflow-y: auto;
}
</style>
