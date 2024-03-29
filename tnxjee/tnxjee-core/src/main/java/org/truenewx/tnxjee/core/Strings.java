package org.truenewx.tnxjee.core;

import org.springframework.util.SystemPropertyUtils;

/**
 * 字符串常量集
 *
 * @author jianglei
 */
public class Strings {

    private Strings() {
    }

    /**
     * 空字符串
     */
    public static final String EMPTY = "";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 句点
     */
    public static final String DOT = ".";
    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";
    /**
     * 空格
     */
    public static final String SPACE = " ";
    /**
     * 等于
     */
    public static final String EQUAL = "=";
    /**
     * 星号
     */
    public static final String ASTERISK = "*";
    /**
     * 双引号
     */
    public static final String DOUBLE_QUOTES = "\"";
    /**
     * 单引号
     */
    public static final String SINGLE_QUOTES = "'";
    /**
     * 回车符
     */
    public static final String ENTER = "\n";
    /**
     * 左括号
     */
    public static final String LEFT_BRACKET = "(";
    /**
     * 右括号
     */
    public static final String RIGHT_BRACKET = ")";
    /**
     * 左方括号
     */
    public static final String LEFT_SQUARE_BRACKET = "[";
    /**
     * 右方括号
     */
    public static final String RIGHT_SQUARE_BRACKET = "]";
    /**
     * 左大括号
     */
    public static final String LEFT_BRACE = "{";
    /**
     * 右大括号
     */
    public static final String RIGHT_BRACE = "}";
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * 斜杠
     */
    public static final String SLASH = "/";
    /**
     * 双斜杠
     */
    public static final String DOUBLE_SLASH = "//";
    /**
     * 反斜杠
     */
    public static final String BACKSLASH = "\\";
    /**
     * 百分号
     */
    public static final String PERCENT = "%";
    /**
     * 问号
     */
    public static final String QUESTION = "?";
    /**
     * 减号
     */
    public static final String MINUS = "-";
    /**
     * 加号
     */
    public static final String PLUS = "+";
    /**
     * 与号
     */
    public static final String AND = "&";

    public static final String AT = "@";

    /**
     * 井号
     */
    public static final String WELL = "#";

    /**
     * 竖杠
     */
    public static final String VERTICAL_BAR = "|";

    /**
     * 无穷大
     */
    public static final String INFINITY = "∞";

    /**
     * 小于
     */
    public static final String LESS_THAN = "<";

    /**
     * 大于
     */
    public static final String GREATER_THAN = ">";

    /**
     * 感叹号
     */
    public static final String EXCLAMATION = "!";

    /**
     * 字符编码：UTF-8
     */
    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * 字符编码：GBK
     */
    public static final String ENCODING_GBK = "GBK";

    /**
     * 字符编码：GB18030
     */
    public static final String ENCODING_GB18030 = "GB18030";

    /**
     * 简体中文
     */
    public static final String LOCALE_SC = "zh_CN";

    /**
     * 繁体中文
     */
    public static final String LOCALE_TC = "zh_TW";

    /**
     * 英文
     */
    public static final String LOCALE_EN = "en";

    public static final String PLACEHOLDER_PREFIX = SystemPropertyUtils.PLACEHOLDER_PREFIX;

    public static final String PLACEHOLDER_SUFFIX = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

    public static final String OS_WINDOWS = "windows";
    public static final String OS_ANDROID = "android";
    public static final String OS_IOS = "ios";
    public static final String OS_MAC = "mac";
    public static final String OS_LINUX = "linux";

}
