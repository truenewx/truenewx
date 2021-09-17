// tnxel.js
/**
 * 基于ElementPlus的扩展支持
 */
import ElementPlus from 'element-plus';
import tnxvue from '../tnxvue.js';
import Icon from './icon/Icon';
import Avatar from './avatar/Avatar';
import Alert from './alert/Alert';

const components = Object.assign({}, tnxvue.components, {
    Icon, Avatar, Alert,
});

const tnxel = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {ElementPlus}),
    components,
});

tnxel.install = tnxel.util.function.around(tnxel.install, function(install, vm) {
    vm.use(ElementPlus);
    install.call(window.tnx, vm);
});

window.tnx = tnxel;

export default tnxel;
