/**
 * 基于WeUI的扩展支持
 */
import tnxvue from '../tnxvue.js';

import Msg from './msg/Msg';

const components = Object.assign({}, tnxvue.components, {Msg});

const tnxwe = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs),
    components,
    install(Vue) {
        Object.keys(components).forEach(key => {
            const component = components[key];
            Vue.component(component.name, component);
        });
    },
});

tnxwe.libs.Vue.use(tnxwe);

window.tnx = tnxwe;

export default tnxwe;
