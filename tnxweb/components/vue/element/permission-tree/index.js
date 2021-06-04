import PermissionTree from './PermissionTree';

PermissionTree.install = function(Vue) {
    Vue.component(PermissionTree.name, PermissionTree);
};

export default PermissionTree;
