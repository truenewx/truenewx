/**
 * 基于WeUI的扩展支持
 */
import tnxvue from '../tnxvue.js';

import Msg from './msg/Msg';

const tnxwe = Object.assign({}, tnxvue, {
    libs: Object.assign({}, tnxvue.libs),
    components: Object.assign({}, tnxvue.components, {Msg}),
});

tnxwe.libs.Vue.use(tnxwe);

window.tnx = tnxwe;

export default tnxwe;
