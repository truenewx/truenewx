// tnxad.js
/**
 * 基于AntDesign 2的扩展支持
 */
import tnxvue from '../tnxvue.js';
import AntDesign from 'ant-design-vue';
import './tnxad.css';

const components = Object.assign({}, tnxvue.components, {});

const tnxad = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {AntDesign}),
    components,
});

tnxad.install = tnxad.util.function.around(tnxad.install, function(install, vm) {
    vm.use(AntDesign);
    install.call(window.tnx, vm);
});

window.tnx = tnxad;

export default tnxad;
