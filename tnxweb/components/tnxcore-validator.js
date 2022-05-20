// tnxcore-validator.js
/**
 * 基于原生js的字段校验器支持
 */
const messages = {
    required: '{0}不能为空',
    notNull: '{0}不能为空',
    notEmpty: '{0}不能为空',
    notBlank: '{0}不能为空',
    maxLength: '{0}长度最多可以有{1}位，已超出{2}位',
    minLength: '{0}长度最少必须有{1}位，还缺少{2}位',
    number: '{0}必须为数字',
    integer: '{0}必须为整数',
    integerLength: '{0}整数位最多可以有{1}位，已超出{2}位',
    scale: '{0}小数位最多可以有{1}位，已超出{2}位',
    maxValue: '{0}最大可以为{1}',
    minValue: '{0}最小可以为{1}',
    email: '{0}只能包含字母、数字、下划线、-和.，@两边各自的长度应小于64',
    idCardNo: '{0}不是正确的身份证号码格式',
    url: '{0}应为格式正确的网址链接绝对路径',
    opposableUrl: '{0}应为格式正确的网址链接绝对路径或相对路径',
    regex: '{0}格式错误{1}',
    notContains: '{0}不能包含：{1}',
    rejectHtmlTags: '{0}不能包含任何html标签',
    allowedHtmlTags: '{0}只能包含html标签：{1}，不可使用其它html标签',
    forbiddenHtmlTags: '{0}不能包含html标签：{1}，可以使用其它html标签',
    cellphone: '{0}不是正确的手机号码格式',
}

export function getErrorMessage(validationName, fieldCaption) {
    let message = messages[validationName];
    if (message) {
        fieldCaption = fieldCaption || '';
        message = message.replace('{0}', fieldCaption);
        for (let i = 2; i < arguments.length; i++) {
            message = message.replace('{' + (i - 1) + '}', arguments[i]);
        }
    }
    return message;
}

const regExps = {
    number: /^-?([1-9]\d{0,2}((,?\d{3})*|\d*)(\.\d*)?|0?\.\d*|0)$/,
    integer: /^(-?[1-9]\d{0,2}(,?\d{3}))|0*$/,
    email: /^[a-zA-Z0-9_\-]([a-zA-Z0-9_\-\.]{0,62})@[a-zA-Z0-9_\-]([a-zA-Z0-9_\-\.]{0,62})$/,
    idCardNo: /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/,
    url: /^https?:\/\/[A-Za-z0-9]+(\.?[A-Za-z0-9_-]+)*(:[0-9]+)?(\/\S*)?$/,
    opposableUrl: /^(https?:\/)?\/[A-Za-z0-9]+(\.?[A-Za-z0-9_-]+)*(:[0-9]+)?(\/\S*)?$/,
    cellphone: /^1[3-9][0-9]{9}$/,
}

function testRegExp(regExpName, fieldValue) {
    if (fieldValue) {
        let regExp = regExps[regExpName];
        return regExp && regExp.test(fieldValue);
    }
    return false;
}

function validateRegExp(regExpName, fieldValue, fieldCaption) {
    if (!testRegExp(regExpName, fieldValue)) {
        return getErrorMessage(regExpName, fieldCaption);
    }
    return undefined;
}

export default {
    getErrorMessage,
    testRegExp,
    validateRegExp,
}
