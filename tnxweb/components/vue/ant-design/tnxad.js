// tnxad.js
/**
 * 基于AntDesign的扩展支持
 */
import tnxvue from '../tnxvue.js';
import AntDesign from 'ant-design-vue';

const components = Object.assign({}, tnxvue.components, {});

const tnxad = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs, {AntDesign}),
    components,
});

tnxad.install = tnxad.util.function.around(tnxad.install, function(install, app) {
    app.use(AntDesign);
    install.call(window.tnx, app);
});

window.tnx = tnxad;

export default tnxad;
