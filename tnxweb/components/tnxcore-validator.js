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
    url: '{0}应为格式正确的网址链接',
    regex: '{0}格式错误{1}',
    notContains: '{0}不能包含：{1}',
    rejectTags: '{0}不能包含任何标签',
    allowedTags: '{0}只能包含标签：{1}',
    forbiddenTags: '{0}不能包含标签：{1}'
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

export default {getErrorMessage}
