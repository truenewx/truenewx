// tnxjq.js
/**
 * 基于jQuery的扩展支持
 */
import $ from 'jquery';
import tnxcore from '../tnxcore.js';

$.toJSON = JSON.stringify;
$.parseJSON = JSON.parse;

/**
 * 滚动到当前元素可见时触发指定处理函数，触发一次后不再触发
 * @param handler 处理函数
 * @param offset 提前触发的偏移量
 */
$.fn.scrollVisible = function(handler, offset) {
    let _this = this;
    let fn = function() {
        offset = offset || 0;
        let top = _this.offset().top;
        let clientheight = document.documentElement.clientHeight;
        let scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
        if (clientheight + scrollTop + offset > top) {
            handler();
            window.removeEventListener('scroll', fn);
        }
    }
    window.addEventListener('scroll', fn);
    $(document).ready(fn);
}

const tnxjq = $.extend({}, tnxcore, {
    libs: $.extend({}, tnxcore.libs, {$})
});

Object.assign(tnxjq.util.dom, {
    /**
     * 获取指定值在指定元素内容中的位置
     * @param element 元素
     * @param value 值
     * @returns {rowIndex: number, columnIndex: number}
     */
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
    /**
     * 将指定元素内部的内容滚动到指定值所在的位置
     * @param element 被滚动的元素
     * @param value 定位的值
     */
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
