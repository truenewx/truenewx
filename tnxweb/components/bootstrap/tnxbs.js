/**
 * 基于BootStrap的扩展
 */
import $ from 'jquery';
import tnxjq from '../jquery/tnxjq.js';

const tnxbs = $.extend({}, tnxjq);

tnxbs.util.dom.breakPoints = {
    xxl: 1400,
    xl: 1200,
    lg: 992,
    md: 768,
    sm: 575,
}

tnxbs.util.dom.getBreakPoint = function() {
    let width = $(window).outerWidth();
    let bps = Object.keys(this.breakPoints);
    for (let bp of bps) {
        if (width >= bps[bp]) {
            return bp;
        }
    }
    return '';
}

tnxbs.util.dom.isBreakPoint = function(bp) {
    return this.getBreakPoint() === bp;
}

tnxbs.util.dom.containsBreakPoint = function(bp) {
    let width = $(window).outerWidth();
    if (bp) {
        let minWidth = this.breakPoints[bp];
        return minWidth && width >= minWidth;
    } else {
        return true;
    }
}

window.tnx = tnxbs;

export default tnxbs;
