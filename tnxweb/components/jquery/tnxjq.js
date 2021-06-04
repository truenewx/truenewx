// tnxjq.js
/**
 * 基于jQuery的扩展支持
 */
import $ from 'jquery';
import tnxcore from '../tnxcore.js';

$.toJSON = JSON.stringify;
$.parseJSON = JSON.parse;

const tnxjq = $.extend({}, tnxcore, {
    libs: $.extend({}, tnxcore.libs, {$})
});

window.tnx = tnxjq;

export default tnxjq;
