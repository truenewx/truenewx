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
    getPosition(element, value) {
        let $element = $(element);
        let lines = value.split('\n');
        let wrap = $element.css('white-space') !== 'nowrap';
        if (wrap) {
            let width = $element.width();
            let fontSize = window.tnx.util.string.getPixelNumber($element.css('font-size'));
            let rowIndex = -1;
            let columnIndex = 0;
            for (let line of lines) {
                if (line.length) {
                    let lineWidth = line.length * fontSize;
                    columnIndex = lineWidth % width;
                    rowIndex += Math.floor(lineWidth / width) + (columnIndex === 0 ? 0 : 1);
                    if (columnIndex === 0) {
                        columnIndex = lineWidth;
                    }
                } else {
                    rowIndex++;
                    columnIndex = 0;
                }
            }
            return {rowIndex, columnIndex};
        } else {
            let lastLine = lines[lines.length - 1];
            return {rowIndex: lines.length - 1, columnIndex: lastLine.length};
        }
    },
    scrollTo(element, value) {
        let position = this.getPosition(element, value);
        let $element = $(element);
        let lineHeight = window.tnx.util.string.getPixelNumber($element.css('line-height'));
        let top = position.rowIndex * lineHeight;
        let height = $element.height();
        if (top > height) {
            element.scrollTop = top - height + lineHeight;
        } else {
            element.scrollTop = 0;
        }
        if ($element.css('white-space') === 'nowrap') {
            let fontSize = window.tnx.util.string.getPixelNumber($element.css('font-size'));
            let left = position.columnIndex * fontSize;
            let width = $element.width();
            if (left > width) {
                element.scrollLeft = left - width;
            } else {
                element.scrollLeft = 0;
            }
        }
    }
});

window.tnx = tnxjq;

export default tnxjq;
