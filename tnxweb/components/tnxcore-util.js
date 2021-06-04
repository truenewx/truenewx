// tnxcore-util.js
import md5 from 'md5';
import base64 from 'base-64';

// 不要在Object.prototype中添加函数，否则vue会报错

Object.assign(Number.prototype, {
    /**
     * 获取当前数值四舍五入到指定精度后的结果
     * @param scale 精度，即小数点后的位数
     * @returns {number} 四舍五入后的结果数值
     */
    halfUp(scale) {
        const p = Math.pow(10, scale);
        return Math.round(this * p) / p;
    },
    toPercent(scale) {
        if (typeof scale === 'number') {
            return (this * 100).halfUp(scale) + '%';
        } else {
            return (this * 100) + '%';
        }
    }
});

Object.assign(String.prototype, {
    firstToLowerCase() {
        return this.substring(0, 1).toLowerCase() + this.substring(1);
    },
    firstToUpperCase() {
        return this.substring(0, 1).toUpperCase() + this.substring(1);
    },
    format(args) {
        if (!(args instanceof Array)) {
            args = arguments;
        }
        let s = this;
        for (let i = 0; i < args.length; i++) {
            s = s.replace('{' + i + '}', args[i]);
        }
        return s;
    },
    contains(searchString) {
        return this.indexOf(searchString) >= 0;
    },
    toCharArray() {
        let array = [];
        for (let i = 0; i < this.length; i++) {
            array.push(this.charAt(i));
        }
        return array;
    },
    // 部分浏览器没有这个方法支持
    replaceAll(regex, replcement) {
        if (typeof regex === 'string') {
            regex = new RegExp(regex, "gm");
        }
        return this.replace(regex, replcement);
    }
});

const DATE_PATTERNS = {
    dateTime: 'yyyy-MM-dd HH:mm:ss',
    date: 'yyyy-MM-dd',
    time: 'HH:mm:ss',
    minute: 'yyyy-MM-dd HH:mm',
    month: 'yyyy-MM',
}

Object.assign(Date.prototype, {
    format(pattern) {
        let date = {
            'M+': this.getMonth() + 1, // 月份
            'd+': this.getDate(), // 日
            'H+': this.getHours(), // 小时
            'm+': this.getMinutes(), // 分
            's+': this.getSeconds(), // 秒
            'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
            'S': this.getMilliseconds(), // 毫秒
        };
        if (/(y+)/.test(pattern)) {
            pattern = pattern.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
        }
        for (let key in date) {
            if (new RegExp('(' + key + ')').test(pattern)) {
                pattern = pattern.replace(RegExp.$1,
                    RegExp.$1.length === 1 ? date[key] : ('00' + date[key]).substr(('' + date[key]).length));
            }
        }
        return pattern;
    },
    formatDateTime() {
        return this.format(DATE_PATTERNS.dateTime);
    },
    formatDate() {
        return this.format(DATE_PATTERNS.date);
    },
    formatTime() {
        return this.format(DATE_PATTERNS.time);
    },
    formatMinute() {
        return this.format(DATE_PATTERNS.minute);
    },
    formatMonth() {
        return this.format(DATE_PATTERNS.month);
    },
    plusDays(days) {
        let millis = this.getTime();
        millis += days * 24 * 60 * 60 * 1000;
        return new Date(millis);
    },
    plusMonths(months) {
        let year = this.getFullYear();
        let month = this.getMonth();
        year += months / 12;
        month += months % 12;
        let date = new Date(this);
        date.setFullYear(year);
        date.setMonth(month);
        return date;
    },
    plusYears(years) {
        let date = new Date(this);
        date.setFullYear(this.getFullYear() + years);
        return date;
    }
});

Object.assign(Array.prototype, {
    contains(element) {
        if (typeof element === 'function') {
            for (let e of this) {
                if (element(e)) {
                    return true;
                }
            }
        } else {
            for (let e of this) {
                if (e === element) {
                    return true;
                }
            }
        }
        return false;
    },
    containsIgnoreCase(element) {
        if (typeof element === 'string') {
            for (let e of this) {
                if (typeof e === 'string' && e.toLocaleLowerCase() === element.toLocaleLowerCase()) {
                    return true;
                }
            }
        }
        return false;
    },
    remove(element) {
        let index = -1;
        if (typeof element === 'function') {
            for (let i = 0; i < this.length; i++) {
                if (element(this[i]) === true) {
                    index = i;
                    break;
                }
            }
        } else {
            for (let i = 0; i < this.length; i++) {
                if (this[i] === element) {
                    index = i;
                    break;
                }
            }
        }
        if (index >= 0) {
            this.splice(index, 1);
        }
        return index;
    },
});

Object.assign(Boolean.prototype, {
    toText() {
        if (this === true) {
            return '是';
        } else if (this === false) {
            return '否';
        }
        return undefined;
    }
});

Object.assign(Element.prototype, {
    /**
     * 获取不是指定标签的第一个子节点
     * @param tagName 标签名
     * @return ChildNode 不是指定标签的第一个子节点，没有则返回undefined
     */
    getFirstChildWithoutTagName(tagName) {
        const children = this.childNodes;
        for (let i = 0; i < children.length; i++) {
            if (children[i].tagName && children[i].tagName !== tagName.toUpperCase()) {
                return children[i];
            }
        }
        return undefined;
    }
});

export const ObjectUtil = {
    toKeyValueArray(object, valueFunction) {
        if (object) {
            let array = [];
            Object.keys(object).forEach(key => {
                let value = object[key];
                if (typeof valueFunction === 'function') {
                    value = valueFunction(value);
                }
                array.push({
                    key: key,
                    value: value,
                });
            });
            return array;
        }
        return undefined;
    },
    setValue(obj, fieldPath, value) {
        var names = fieldPath.split('.');
        for (let i = 0; i < names.length - 1; i++) {
            let name = names[i];
            obj[name] = obj[name] || {};
            obj = obj[name];
        }
        let lastName = names[names.length - 1];
        obj[lastName] = value;
        return obj;
    },
    deepClone(obj) {
        if (obj) {
            let json = StringUtil.toJson(obj);
            return StringUtil.parseJson(json);
        }
        return obj;
    }
}

export const FunctionUtil = {
    around(target, around) {
        const _this = this;
        return function() {
            const args = [target];
            for (let i = 0; i < arguments.length; i++) {
                args.push(arguments[i]);
            }
            return around.apply(_this, args);
        }
    },
    /**
     * 最少超时回调
     * @param beginTime 开始时间
     * @param callback 回调函数
     * @param minTimeout 最少超时时间，单位：毫秒
     */
    setMinTimeout(beginTime, callback, minTimeout) {
        if (beginTime instanceof Date) {
            beginTime = beginTime.getTime();
        }
        minTimeout = minTimeout || 1500;
        const dTime = new Date().getTime() - beginTime;
        if (dTime > minTimeout) {
            callback();
        } else {
            setTimeout(callback, minTimeout - dTime);
        }
    },
}

export const MathUtil = {
    /**
     * 获取在[min,max)范围内的随机整数值
     * @param min 最小值
     * @param max 最大值
     * @returns {number} 随机整数值
     */
    randomInt(min, max) {
        if (min > max) { // 最小值如果大于最大值，则互换
            let temp = min;
            min = max;
            max = temp;
        }
        let result = Math.ceil(min + (max - min) * Math.random()); // 用ceil()方法以确保结果一定不小于最小值
        if (result >= max) { // 确保不大于最大值
            result = max;
        }
        return result;
    },
}

export const StringUtil = {
    toJson: JSON.stringify,
    parseJson: JSON.parse,
    random(length, chars) {
        if (length >= 0) {
            chars = chars || 'abcdefghijklmnopqrstuvwxyz'; // 默认取值范围为所有小写字母
            let s = '';
            for (let i = 0; i < length; i++) {
                s += chars.charAt(MathUtil.randomInt(0, chars.length));
            }
            return s;
        }
        return undefined;
    },
    getCapacityCaption(capacity, scale) {
        if (typeof capacity === 'number') {
            scale = scale || 0;
            const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
            let series = 0;
            for (series = 0; series < units.length; series++) {
                if (capacity >= 1024) {
                    capacity = (capacity / 1024).halfUp(scale);
                } else {
                    break;
                }
            }
            return capacity + units[series];
        }
        return undefined;
    },
    getPixelString(value, containerValue) {
        if (typeof value === 'number') {
            return value + 'px';
        } else if (typeof value === 'string') {
            if (value.endsWith('%')) {
                let percent = parseFloat(value.substr(0, value.length - 1));
                let containerNumber = this.getPixelNumber(containerValue);
                if (!isNaN(percent) && !isNaN(containerNumber)) {
                    return (containerNumber * percent / 100) + 'px';
                }
            }
        }
        return value;
    },
    getPixelNumber(value) {
        if (typeof value === 'number') {
            return value;
        }
        if (typeof value === 'string') {
            if (value.toLowerCase().endsWith('px')) {
                return parseInt(value.substr(0, value.length - 2));
            }
        }
        return NaN;
    },
    idCard(idCardNo) {
        let birthday;
        let male;
        if (idCardNo.length === 15) { // 15位身份证号码
            birthday = '19' + idCardNo.substr(6, 6);
            male = idCardNo.substr(14, 1);
        } else {
            birthday = idCardNo.substr(6, 8);
            male = idCardNo.substr(16, 1);
        }
        birthday = birthday.substr(0, 4) + '-' + birthday.substr(4, 2) + '-' + birthday.substr(6, 2);
        return {
            birthday: birthday,
            male: male === '1',
            serialNo: idCardNo
        };
    },
    matchesForEach(content, keyword) {
        if (!keyword) { // 搜索关键字为空，则全部匹配
            return true;
        }
        if (content !== undefined && content !== null) {
            content += '';
        }
        if (!content) { // 搜索内容为空，则无法匹配
            return false;
        }
        let index = 0;
        for (let c of keyword.toCharArray()) {
            index = content.indexOf(c, index);
            if (index < 0) { // 搜索关键字中有一个字符未被包含在内容中，则不匹配
                return false;
            }
        }
        // 遍历后没有不匹配的字符，则说明整理匹配
        return true;
    }
}

export const DateUtil = {
    patterns: DATE_PATTERNS,
    format(date, pattern) {
        if (typeof date === 'number') {
            date = new Date(date);
        }
        if (date instanceof Date) {
            return date.format(pattern);
        }
        return undefined;
    },
    formatDate(date) {
        return this.format(date, DATE_PATTERNS.date);
    },
    formatTime(date) {
        return this.format(date, DATE_PATTERNS.time);
    },
    formatDateTime(date) {
        return this.format(date, DATE_PATTERNS.dateTime);
    },
    formatMinute(date) {
        return this.format(date, DATE_PATTERNS.minute);
    },
    /**
     * 将指定yyyy-MM-dd型的日期转换为yyyy-MM的月份格式
     * @param date 日期
     * @returns 月份
     */
    dateToMonth(date) {
        if (date instanceof Date) {
            return this.format(date, this.pattern.month);
        }
        if (date) {
            return date.substr(0, date.lastIndexOf('-'));
        }
        return date;
    },
    createDate(year, month, day, hour, minute, second, millis) {
        let date = new Date();
        date.setFullYear(year, (month || 1) - 1, day || 1);
        date.setHours(hour || 0, minute || 0, second || 0, millis || 0);
        return date;
    },
    getDaysOfMonth(year, month) {
        // 闰月
        if (month === 2 && ((year % 400 === 0) || (year % 4 === 0 && year % 100 !== 0))) {
            return 29;
        }
        return [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
    },
    betweenDays(earlierDate, laterDate) {
        earlierDate = new Date(earlierDate);
        laterDate = new Date(laterDate);
        // 如果earlierDate晚于laterDate，则返回负值
        return (laterDate.getTime() - earlierDate.getTime()) / 1000 / 60 / 60 / 24;
    },
    betweenDate(earlierDate, laterDate) {
        earlierDate = new Date(earlierDate);
        let year1 = earlierDate.getFullYear();
        let month1 = earlierDate.getMonth() + 1;
        let day1 = earlierDate.getDate();

        laterDate = new Date(laterDate);
        let year2 = laterDate.getFullYear();
        let month2 = laterDate.getMonth() + 1;
        let day2 = laterDate.getDate();

        let revisedMonths = 0; //修订月数
        let revisedYears = 0; // 修订年数

        // 计算日，不足时向月份借
        let days;
        if (day2 >= day1) {
            days = day2 - day1;
        } else {
            revisedMonths = -1;
            days = this.getDaysOfMonth(year1, month1) + day2 - day1;
        }

        // 计算月，不足时向年份借
        let months;
        if (month2 + revisedMonths >= month1) {
            months = month2 + revisedMonths - month1;
        } else {
            revisedYears = -1;
            months = 12 + month2 + revisedMonths - month1;
        }

        // 计算年
        let years = year2 + revisedYears - year1;

        return {
            years,
            months,
            days,
            toMonthString() {
                let s = '';
                if (this.years > 0) {
                    s += this.years + '年';
                }
                s += this.months + '个月';
                return s;
            },
            toString() {
                let s = '';
                if (this.years > 0) {
                    s += this.years + '年';
                }
                if (this.months > 0) {
                    s += this.months + '个月';
                } else if (this.years > 0 && this.days > 0) {
                    s += '零';
                }
                if (this.days > 0) {
                    s += this.days + '天';
                }
                return s;
            }
        }
    },
    today() {
        let date = new Date();
        date.setHours(0, 0, 0, 0);
        return date;
    },
    monthsToYearMonth(months) {
        if (months !== undefined && months !== null) {
            months = parseInt(months);
            if (!isNaN(months)) {
                let year = Math.floor(months / 12);
                let month = months % 12;
                return (year >= 1 ? year + '年' : '') + month + '个月';
            }
        }
        return undefined;
    }
}

export const NetUtil = {
    /**
     * 从指定头信息集中获取指定头信息值
     * @param headers 头信息集
     * @param name 头信息名称
     * @param defaultValue 默认值
     * @returns {undefined|*} 头信息值
     */
    getHeader(headers, name, defaultValue) {
        if (headers && name) {
            return headers[name] || headers[name.toLowerCase()] || defaultValue;
        }
        return undefined;
    },
    getParameterString() {
        let href = window.location.href;
        let index = href.indexOf('?');
        if (index >= 0) {
            let parameterString = href.substr(index + 1);
            index = parameterString.indexOf('#');
            if (index >= 0) {
                parameterString = parameterString.substr(0, index);
            }
            return parameterString;
        }
        return '';
    },
    getParameters() {
        let params = {};
        let parameterString = this.getParameterString();
        if (parameterString) {
            let array = parameterString.split('&');
            for (let parameter of array) {
                let index = parameter.indexOf('=');
                if (index > 0) {
                    let paramName = parameter.substr(0, index);
                    let paramValue = parameter.substr(index + 1);
                    if (params[paramName]) {
                        params[paramName] = [params[paramName]];
                        params[paramName].push(paramValue);
                    } else {
                        params[paramName] = paramValue;
                    }
                }
            }
        }
        return params;
    },
    getParamValue(name) {
        let parameterString = this.getParameterString();
        if (parameterString) {
            let array = parameterString.split('&');
            for (let parameter of array) {
                let index = parameter.indexOf('=');
                if (index > 0) {
                    let paramName = parameter.substr(0, index);
                    if (paramName === name) {
                        return parameter.substr(index + 1);
                    }
                }
            }
        }
        return undefined;
    },
    /**
     * 将指定对象中的所有字段拼凑成形如a=a1&b=b1的字符串
     * @param object 对象
     * @returns {string} 拼凑成的字符串
     */
    toParameterString(object) {
        let s = '';
        if (typeof object === 'object') {
            Object.keys(object).forEach(key => {
                let value = object[key];
                switch (typeof value) {
                    case 'function':
                        value = value();
                        break;
                    case 'object':
                        if (typeof value.toString === 'function') {
                            value = value.toString();
                        } else {
                            value = null;
                        }
                        break;
                }
                if (value) {
                    s += '&' + key + '=' + value;
                }
            });
            if (s.length) { // 去掉头部多余的&
                s = s.substr(1);
            }
        }
        return s;
    },
    /**
     * 为指定URL附加参数
     * @param url 原URL
     * @param params 附加的参数集
     * @return {string} 新的URL
     */
    appendParams(url, params) {
        if (typeof url === 'string') {
            let parameterString = this.toParameterString(params);
            if (parameterString.length) {
                return url += (url.contains('?') ? '&' : '?') + parameterString;
            }
        }
        return url;
    },
    /**
     * 为指定URL附加一个随机参数，用于刷新资源
     * @param url URL
     * @return {string} 新的URL
     */
    appendRandomParam(url) {
        let params = {};
        let key = '_' + StringUtil.random(8);
        params[key] = new Date().getTime();
        return this.appendParams(url, params);
    },
    getAnchor() {
        const anchor = window.location.hash;
        if (anchor) {
            const index = anchor.indexOf('#');
            if (index >= 0) {
                return anchor.substr(index + 1);
            }
        }
        return undefined;
    },
    isIntranetHostname(hostname) {
        if (hostname === 'localhost' || hostname === '127.0.0.1' || hostname === '0:0:0:0:0:0:0:1') { // 本机
            return true;
        }
        if (hostname.startsWith('192.168.') || hostname.startsWith('10.')) { // 192.168网段或10网段
            return true;
        } else if (hostname.startsWith('172.')) { // 172.16-172.31网段
            let seg = hostname.substring(4, hostname.indexOf('.', 4)); // 取第二节
            let value = parseInt(seg);
            return 16 <= value && value <= 31;
        }
        return false;
    },
    isUrl(s) {
        const regex = '^((https|http|ftp)?://)'
            + '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@
            + '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184
            + '|' // 允许IP和DOMAIN（域名）
            + '([0-9a-z_!~*\'()-]+.)*' // 二级域名：www
            + '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 一级域名
            + '[a-z]{2,6})' // 域名后缀：.com
            + '(:[0-9]{1,4})?' // 端口：80
            + '((/?)|'
            + '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$';
        return new RegExp(regex).test(s);
    },
}

export const DomUtil = {
    getMetaContent(name) {
        const meta = document.querySelector('meta[name="' + name + '"]');
        if (meta) {
            return meta.getAttribute('content');
        }
        return undefined;
    },
    getDocWidth() {
        return document.documentElement.clientWidth;
    },
    getDocHeight() {
        return document.documentElement.clientHeight;
    },
    maxZIndex(elements) {
        let result = -1;
        elements.forEach(function(element) {
            const zIndex = Number(element.style.zIndex);
            if (result < zIndex) {
                result = zIndex;
            }
        });
        return result;
    },
    /**
     * 获取最小的可位于界面顶层的ZIndex
     */
    minTopZIndex(step) {
        step = step || 1;
        const maxValue = 2147483584; // 允许的最大值，取各浏览器支持的最大值中的最小一个（Opera）
        const elements = document.body.querySelectorAll('*');
        const maxZIndex = this.maxZIndex(elements); // 可见DOM元素中的最高层级
        if (maxZIndex > maxValue - step) {
            return maxValue;
        } else {
            return maxZIndex + step;
        }
    },
}

export const util = {
    md5: md5,
    base64: base64,
    object: ObjectUtil,
    function: FunctionUtil,
    math: MathUtil,
    string: StringUtil,
    date: DateUtil,
    net: NetUtil,
    dom: DomUtil,
};

export default util;
