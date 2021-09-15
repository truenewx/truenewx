// tnxel.js
/**
 * 基于ElementPlus的扩展支持
 */
import ElementPlus from 'element-plus';
import tnxvue from '../tnxvue.js';

const components = Object.assign({}, tnxvue.components, {});

const tnxel = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {ElementPlus}),
    components,
});

tnxel.install = tnxel.util.function.around(tnxel.install, function(install, vue) {
    vue.use(ElementPlus);
    install.call(window.tnx, vue);
});

window.tnx = tnxel;

export default tnxel;
