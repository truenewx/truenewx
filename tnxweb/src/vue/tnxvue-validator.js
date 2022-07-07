// tnxvue-validator.js
/**
 * 校验规则转换器，将服务端元数据中的校验规则转换为async-validator组件的规则。
 * async-validator组件详见：https://github.com/yiminghe/async-validator
 */
import validator from '../tnxcore-validator';
import AsyncValidator from 'async-validator';

/**
 * async-validator组件支持的类型清单
 */
const ruleTypes = ['string', 'number', 'boolean', 'method', 'regexp', 'integer', 'float', 'array', 'object', 'enum',
    'date', 'url', 'hex', 'email', 'any'];

function getRuleType(metaType) {
    if (ruleTypes.contains(metaType)) {
        return metaType;
    }
    switch (metaType) {
        case 'decimal':
            return 'float';
        case 'regex':
            return 'regexp';
        case 'datetime':
        case 'time':
            return 'date';
    }
    return ruleTypes[0];
}

function getRule(validationName, validationValue, fieldMeta) {
    let rule = undefined;
    let fieldCaption = '';
    // 据目前观察，字段格式校验的错误消息均显示在字段旁，无需显示字段名称，未来如果出现不在字段旁显示的场景，再考虑扩展
    // if (fieldMeta && fieldMeta.caption) {
    //     fieldCaption = fieldMeta.caption;
    // }
    switch (validationName) {
        case 'required':
        case 'notNull':
        case 'notEmpty':
        case 'notBlank':
            if (validationValue === true) {
                rule = {
                    required: true,
                    validator(r, fieldValue, callback, source, options) {
                        if (validationValue) {
                            let blank = fieldValue === undefined || fieldValue === null;
                            if (!blank) {
                                if (Array.isArray(fieldValue)) {
                                    blank = fieldValue.length === 0; // 数组长度为0视为空
                                } else if (typeof fieldValue === 'string') {
                                    blank = fieldValue.trim().length === 0; // 字符串去掉两端空格后长度为0视为空
                                } else if (typeof fieldValue === 'number') {
                                    blank = isNaN(fieldValue); // 非法的数字视为空
                                } else if (typeof fieldValue === 'object') {
                                    // 对象非日期，且没有字段的视为空
                                    blank = !(fieldValue instanceof Date) && Object.keys(fieldValue).length === 0;
                                    if (!blank) {
                                        // 可永久的日期对象，非永久且日期值为空时视为空
                                        if (typeof fieldValue.permanent === 'boolean') {
                                            blank = !fieldValue.permanent && !fieldValue.value;
                                        }
                                    }
                                }
                            }
                            if (blank) {
                                let message = validator.getErrorMessage(validationName, fieldCaption);
                                return callback(new Error(message));
                            }
                        }
                        return callback();
                    }
                }
            }
            break;
        case 'minLength':
            rule = {
                validator(r, fieldValue, callback, source, options) {
                    if (typeof validationValue === 'number' && typeof fieldValue === 'string') {
                        // 回车符计入长度
                        let enterLength = fieldValue.indexOf('\n') < 0 ? 0 : fieldValue.match(/\n/g).length;
                        let fieldLength = fieldValue.length + enterLength;
                        if (fieldLength < validationValue) {
                            let message = validator.getErrorMessage(validationName, fieldCaption,
                                validationValue, validationValue - fieldLength);
                            return callback(new Error(message));
                        }
                    }
                    return callback();
                }
            };
            break;
        case 'maxLength':
            rule = {
                validator(r, fieldValue, callback, source, options) {
                    if (typeof validationValue === 'number' && typeof fieldValue === 'string') {
                        // 回车符计入长度
                        let enterLength = fieldValue.indexOf('\n') < 0 ? 0 : fieldValue.match(/\n/g).length;
                        let fieldLength = fieldValue.length + enterLength;
                        if (fieldLength > validationValue) {
                            let message = validator.getErrorMessage(validationName, fieldCaption,
                                validationValue, fieldLength - validationValue);
                            return callback(new Error(message));
                        }
                    }
                    return callback();
                }
            };
            break;
        case 'number':
            if (validationValue === true) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue && typeof fieldValue === 'string') {
                            if (!/^[0-9]+$/.test(fieldValue)) {
                                let message = validator.getErrorMessage(validationName, fieldCaption);
                                return callback(new Error(message));
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'notContainsHtmlChars':
            if (validationValue === true) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            let limitedValues = ['<', '>', '\'', '"', '\\'];
                            for (let i = 0; i < limitedValues.length; i++) {
                                if (fieldValue.indexOf(limitedValues[i]) >= 0) {
                                    let s = limitedValues.join(' ');
                                    let message = validator.getErrorMessage('notContains', fieldCaption, s);
                                    return callback(new Error(message));
                                }
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'notContainsIllegalFilenameChars':
            if (validationValue === true) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            let limitedValues = ['/', '\\', ':', '*', '?', '"', '<', '>', '|'];
                            for (let i = 0; i < limitedValues.length; i++) {
                                if (fieldValue.indexOf(limitedValues[i]) >= 0) {
                                    let s = limitedValues.join(' ');
                                    let message = validator.getErrorMessage('notContains', fieldCaption, s);
                                    return callback(new Error(message));
                                }
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'notContains':
            if (validationValue) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            let limitedValues = validationValue.split(' ');
                            for (let i = 0; i < limitedValues.length; i++) {
                                if (fieldValue.indexOf(limitedValues[i]) >= 0) {
                                    let message = validator.getErrorMessage('notContains', fieldCaption,
                                        validationValue);
                                    return callback(new Error(message));
                                }
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'rejectHtmlTags':
            if (validationValue === true) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            if (/<[a-z]+[ ]*[/]?[ ]*>/gi.test(fieldValue)) {
                                let message = validator.getErrorMessage(validationName, fieldCaption);
                                return callback(new Error(message));
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'allowedHtmlTags':
            if (validationValue) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            let tags = validationValue.toLowerCase().split(',');
                            if (tags.length) {
                                fieldValue = fieldValue.toLowerCase();
                                let leftIndex = fieldValue.indexOf('<');
                                let rightIndex = leftIndex >= 0 ? fieldValue.indexOf('>', leftIndex) : -1;
                                while (leftIndex >= 0 && rightIndex >= 0) {
                                    let sub = fieldValue.substring(leftIndex + 1, rightIndex); // <>中间的部分
                                    let spaceIndex = sub.indexOf(' ');
                                    let tag = spaceIndex >= 0 ? sub.substring(0, spaceIndex) : sub;
                                    if (tag.startsWith('/')) { // 标签结束处
                                        tag = tag.substring(1);
                                    }
                                    if (!tags.contains(tag.toLowerCase())) {
                                        let message = validator.getErrorMessage(validationName, fieldCaption,
                                            tags.join(', '));
                                        return callback(new Error(message)); // 存在不允许的标签，则报错
                                    }
                                    leftIndex = fieldValue.indexOf('<', rightIndex);
                                    rightIndex = leftIndex >= 0 ? fieldValue.indexOf('>', leftIndex) : -1;
                                }
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'forbiddenHtmlTags':
            if (validationValue) {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        if (fieldValue) {
                            let tags = validationValue.toLowerCase().split(',');
                            if (tags.length) {
                                fieldValue = fieldValue.toLowerCase();
                                for (let tag of tags) {
                                    if (fieldValue.contains('<' + tag + '>') || fieldValue.contains('<' + tag + ' ')) {
                                        let message = validator.getErrorMessage(validationName, fieldCaption,
                                            tags.join(', '));
                                        return callback(new Error(message));
                                    }
                                }
                            }
                        }
                        return callback();
                    }
                };
            }
            break;
        case 'email':
            if (validationValue === true) {
                rule = {
                    type: validationName,
                    message: validator.getErrorMessage(validationName, fieldCaption),
                }
            }
            break;
        case 'cellphone':
        case 'idCardNo':
        case 'url':
        case 'opposableUrl':
            rule = {
                validator(r, fieldValue, callback, source, options) {
                    if (validationValue) {
                        let message = validator.validateRegExp(validationName, fieldValue, fieldCaption);
                        if (message) {
                            return callback(new Error(message));
                        }
                    }
                    return callback();
                }
            };
            break;
        case 'regex':
            rule = {
                validator(r, fieldValue, callback, source, options) {
                    if (fieldValue) {
                        let pattern = validationValue[0];
                        // 服务端正则表达式无需以^$作为首尾，客户端需确保以^$作为首尾
                        if (!pattern.startsWith('^')) {
                            pattern = '^' + pattern;
                        }
                        if (!pattern.endsWith('$')) {
                            pattern += '$';
                        }
                        let regexp = new RegExp(pattern, 'gi');
                        if (!regexp.test(fieldValue)) {
                            let message = validationValue[1];
                            if (message) {
                                message = fieldCaption + message;
                            } else {
                                message = validator.getErrorMessage('regex', fieldCaption, '');
                            }
                            return callback(new Error(message));
                        }
                    }
                    return callback();
                }
            }
            break;
        case 'custom':
            if (typeof validationValue === 'function') {
                rule = {
                    validator(r, fieldValue, callback, source, options) {
                        let message = validationValue(fieldValue);
                        if (message) {
                            return callback(new Error(message));
                        }
                        return callback();
                    }
                }
            }
            break;
    }
    if (rule) {
        rule.name = validationName;
        let metaType = 'text';
        if (fieldMeta?.type) {
            metaType = fieldMeta.type.toLowerCase();
        }
        rule.type = rule.type || getRuleType(metaType);
        let optional = metaType === 'option' || metaType === 'datetime' || metaType === 'date' || metaType === 'time';
        rule.trigger = optional ? 'change' : 'blur';
    }
    return rule;
}

/**
 * 从服务端元数据中构建完整的规则集
 * @param meta
 * @returns {{}}
 */
export function getRules(meta) {
    let rules = {};
    Object.keys(meta).forEach(fieldName => {
        let fieldMeta = meta[fieldName];
        if (fieldMeta.validation) {
            let fieldRules = [];
            Object.keys(fieldMeta.validation).forEach(validationName => {
                let validationValue = fieldMeta.validation[validationName];
                let rule = getRule(validationName, validationValue, fieldMeta);
                if (rule) {
                    fieldRules.push(rule);
                }
            });
            // 将可能包含的引用字段路径中的.替换为__，以符合async-validator规则名称的规范
            let ruleName = fieldName.replace('.', '__');
            rules[ruleName] = fieldRules;
        }
    });
    return rules;
}

export default {
    getErrorMessage: validator.getErrorMessage,
    testRegExp: validator.testRegExp,
    validateRegExp: validator.validateRegExp,
    getRule,
    getRules,
    validate: function(rules, source, callback) {
        return new AsyncValidator(rules).validate(source, callback);
    },
}
