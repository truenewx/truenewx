package org.truenewx.tnxjee.model.validation.constraint.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.model.validation.constraint.HtmlTagLimit;

/**
 * Html标签限定校验器
 *
 * @author jianglei
 */
public class HtmlTagLimitValidator implements ConstraintValidator<HtmlTagLimit, CharSequence> {

    private String[] allowed;
    private String[] forbidden;

    @Override
    public void initialize(HtmlTagLimit annotation) {
        this.allowed = annotation.allowed();
        ArrayUtil.toLowerCase(this.allowed);
        this.forbidden = annotation.forbidden();
        ArrayUtil.toLowerCase(this.forbidden);
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        String s = value == null ? null : value.toString();
        if (StringUtils.isNotBlank(s) && s.contains(Strings.LESS_THAN) && s.contains(Strings.GREATER_THAN)) {
            s = s.trim();
            if (ArrayUtils.isEmpty(this.allowed) && ArrayUtils.isEmpty(this.forbidden)) { // 限制所有标签
                return !StringUtil.regexMatch(s, "<[a-z]+[ ]*[/]?[ ]*>");
            }
            // 允许的标签清单不为空，才限制允许标签外的其它标签
            if (ArrayUtils.isNotEmpty(this.allowed) && containsOtherTag(s, this.allowed)) {
                return false; // 存在不允许的标签，则直接返回false
            }
            if (containsForbiddenTag(s, this.forbidden)) {
                return false; // 存在禁止的标签，则直接返回false
            }
        }
        return true;
    }

    /**
     * 判断指定字符串是否包含允许使用的标签清单之外的其它标签
     *
     * @param s           字符串
     * @param allowedTags 允许使用的标签清单
     * @return 指定字符串是否包含允许使用的标签清单之外的其它标签
     */
    public static boolean containsOtherTag(String s, String[] allowedTags) {
        // 正则表达式写不出，只得用笨办法
        int leftIndex = s.indexOf(Strings.LESS_THAN);
        int rightIndex = leftIndex >= 0 ? s.indexOf(Strings.GREATER_THAN, leftIndex) : -1;
        while (leftIndex >= 0 && rightIndex >= 0) {
            String sub = s.substring(leftIndex + 1, rightIndex); // <>中间的部分
            int spaceIndex = sub.indexOf(Strings.SPACE);
            String tag = spaceIndex >= 0 ? sub.substring(0, spaceIndex) : sub;
            if (tag.startsWith(Strings.SLASH)) { // 标签结束处
                tag = tag.substring(Strings.SLASH.length());
            }
            // 全部为英文字母的才可能是标签。存在标签但不在允许的标签清单（即使为空）中，则返回true
            if (StringUtil.isLetters(tag) && !ArrayUtils.contains(allowedTags, tag.toLowerCase())) {
                return true;
            }
            leftIndex = s.indexOf(Strings.LESS_THAN, rightIndex);
            rightIndex = leftIndex >= 0 ? s.indexOf(Strings.GREATER_THAN, leftIndex) : -1;
        }
        return false;
    }

    /**
     * 判断指定字符串是否包含禁止使用的标签
     *
     * @param s             字符串
     * @param forbiddenTags 禁止使用的标签清单
     * @return 指定字符串是否包含禁止使用的标签
     */
    public static boolean containsForbiddenTag(String s, String[] forbiddenTags) {
        if (ArrayUtils.isNotEmpty(forbiddenTags)) { // 禁止的标签
            // 无漏洞的正则表达式难以理解，还是以字符串操作进行判断
            s = s.toLowerCase();
            for (String tag : forbiddenTags) {
                if (s.contains(Strings.LESS_THAN + tag + Strings.GREATER_THAN) || s.contains(
                        Strings.LESS_THAN + tag + Strings.SPACE)) {
                    return true;
                }
            }
        }
        return false;
    }

}
