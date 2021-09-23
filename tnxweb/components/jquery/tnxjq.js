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

Object.assign(tnxjq.util.dom, {
    getRowIndex(element, value) {
        let $element = $(element);
        let width = $element.width();
        let fontSize = window.tnx.util.string.getPixelNumber($element.css('font-size'));
        let rowIndex = -1;
        let lines = value.split('\n');
        for (let line of lines) {
            if (line.length) {
                let lineWidth = line.length * fontSize;
                rowIndex += Math.floor(lineWidth / width) + (lineWidth % width === 0 ? 0 : 1);
            } else {
                rowIndex++;
            }
        }
        return rowIndex;
    },
    scrollTo(element, value) {
        let rowIndex = this.getRowIndex(element, value);
        let $element = $(element);
        let lineHeight = window.tnx.util.string.getPixelNumber($element.css('line-height'));
        let top = rowIndex * lineHeight;
        let height = $element.height();
        if (top > height) {
            element.scrollTop = top - height + lineHeight;
        }
    }
});

window.tnx = tnxjq;

export default tnxjq;
