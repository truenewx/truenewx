package org.truenewx.tnxjee.core.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.truenewx.tnxjee.core.Strings;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * 字符串工具类
 *
 * @author jianglei
 */
public class StringUtil {

    /**
     * 随机字符串类型：纯数字
     */
    public static final int RANDOM_TYPE_NUMBER = 1;
    /**
     * 随机字符串类型：纯字母
     */
    public static final int RANDOM_TYPE_LETTER = 2;
    /**
     * 随机字符串类型：混合
     */
    public static final int RANDOM_TYPE_MIXED = 3;

    /**
     * 表示IPv4地址的正则表达式
     */
    public static final String IPv4_PATTERN =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|22[0-3])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

    /**
     * 表示IPv6地址的正则表达式
     */
    public static final String IPv6_PATTERN = "\\s*((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4})|:))|"
            + "(([0-9A-Fa-f]{1,4}:){6}(:|((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})){3})|(:[0-9A-Fa-f]{1,4})))|(([0-9A-Fa-f]{1,4}:){5}((:((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|"
            + "(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}((:((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|"
            + "(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}((:((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|"
            + "(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}((:((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|"
            + "(([0-9A-Fa-f]{1,4}:)(:[0-9A-Fa-f]{1,4}){0,4}((:((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|"
            + "(:(:[0-9A-Fa-f]{1,4}){0,5}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|"
            + "2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))|(((25[0-5]|2[0-4]\\d|"
            + "[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})))(%.+)?\\s*";

    /**
     * 表示IP地址的正则表达式，包含IPv4和IPv6
     */
    public static final String IP_PATTERN = "(" + IPv4_PATTERN + ")|(" + IPv6_PATTERN + ")";

    /**
     * 表示标准URL的正则表达式
     */
    public static final String URL_PATTERN =
            "^((https?://)|(www\\.))[\\w-]+(\\.[\\w-]+)*(:\\d+)?(/[\\w-=~]+)*(/[\\w-=~]+(\\.\\w+)?)?/?(\\?\\w+(\\.\\w+)?=[\\da-z_\\.;#@%\\-]*(&\\w+(\\.\\w+)?=[\\da-z_\\.;#@%\\-]*)*)?$";

    public static final String EMAIL_PATTERN = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    public static final String CELLPHONE_PATTERN = "^1\\d{10}$";

    public static final String ID_CARD_NO_PATTERN = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";

    public static final String REGION_CODE_PATTERN = "^[A-Z]{2}[0-9]{6}$";

    public static final String CHINESE_PATTERN = "^[\\u4E00-\\u9FA5]+$";

    private static final PathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Map<String, ResourceBundle> resourceBundleCache = new Hashtable<>();

    private static final Pattern PATTERN_UPPER_CHAR = Pattern.compile("[A-Z]");

    private StringUtil() {
    }

    /**
     * 校验指定字符串是否匹配指定正则表达式
     *
     * @param s       字符串
     * @param pattern 正则表达式
     * @return true if 指定字符串匹配指定正则表达式, otherwise false
     */
    public static boolean regexMatch(String s, String pattern) {
        try {
            return Pattern.matches(pattern, s);
        } catch (PatternSyntaxException e) {
        }
        return false;
    }

    /**
     * 生成随机字符串。其中type指定随机字符串类型，取值范围: RANDOM_TYPE_NUMBER, RANDOM_TYPE_LETTER,
     * RANDOM_TYPE_MIXED
     *
     * @param type   随机字符串类型
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String random(int type, int length) {
        byte[] b = new byte[length];
        switch (type) {
            case RANDOM_TYPE_NUMBER: {
                for (int i = 0; i < b.length; i++) {
                    b[i] = MathUtil.randomByte((byte) '0', (byte) '9');
                }
                break;
            }
            case RANDOM_TYPE_LETTER: {
                Random random = new Random();
                for (int i = 0; i < b.length; i++) {
                    b[i] = MathUtil.randomByte((byte) 'a', (byte) 'z');
                    if (random.nextBoolean()) {
                        b[i] = MathUtil.randomByte((byte) 'A', (byte) 'Z');
                    }
                }
                break;
            }
            case RANDOM_TYPE_MIXED: {
                Random random = new Random();
                for (int i = 0; i < b.length; i++) {
                    b[i] = MathUtil.randomByte((byte) '0', (byte) '9');
                    if (random.nextBoolean()) {
                        b[i] = MathUtil.randomByte((byte) 'a', (byte) 'z');
                    }
                    if (random.nextBoolean()) {
                        b[i] = MathUtil.randomByte((byte) 'A', (byte) 'Z');
                    }
                }
                break;
            }
        }
        return new String(b);
    }

    /**
     * 判断指定字符串中是否包含指定比较字符集合中的字符
     *
     * @param s     字符串
     * @param chars 比较字符集合
     * @return true if 指定字符串中包含指定字符集合中的字符, otherwise false
     */
    public static boolean containsChar(String s, String chars) {
        if (StringUtils.isEmpty(chars)) {
            return false;
        }
        byte[] bc = chars.getBytes();
        for (byte b : bc) {
            if (s.indexOf((char) b) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成纯字母组合的随机字符串
     *
     * @param length       长度
     * @param ingoredChars 要忽略的字符集合
     * @return 纯字母组合的随机字符串
     */
    public static String randomLetters(int length, String ingoredChars) {
        String s = random(StringUtil.RANDOM_TYPE_LETTER, length);
        while (containsChar(s, ingoredChars)) {
            s = random(StringUtil.RANDOM_TYPE_LETTER, length);
        }
        return s;
    }

    /**
     * 生成纯数字组合的随机字符串
     *
     * @param length       长度
     * @param ingoredChars 要忽略的字符集合
     * @return 纯数字组合的随机字符串
     */
    public static String randomNumbers(int length, String ingoredChars) {
        String s = random(StringUtil.RANDOM_TYPE_NUMBER, length);
        while (containsChar(s, ingoredChars)) {
            s = random(StringUtil.RANDOM_TYPE_NUMBER, length);
        }
        return s;
    }

    /**
     * 生成数字和字母混合的随机字符串
     *
     * @param length       长度
     * @param ingoredChars 要忽略的字符集合
     * @return 数字和字母混合的随机字符串
     */
    public static String randomMixeds(int length, String ingoredChars) {
        String s = random(StringUtil.RANDOM_TYPE_MIXED, length);
        while (containsChar(s, ingoredChars)) {
            s = random(StringUtil.RANDOM_TYPE_MIXED, length);
        }
        return s;
    }

    /**
     * 将指定字符串的首字母转换为大写，返回新的字符串
     *
     * @param s 字符串
     * @return 首字母大写的新字符串
     */
    public static String firstToUpperCase(String s) {
        if (StringUtils.isNotEmpty(s)) {
            char first = s.charAt(0);
            if (Character.isLowerCase(first)) {
                StringBuffer sb = new StringBuffer(s);
                sb.setCharAt(0, Character.toUpperCase(first));
                return sb.toString();
            }
        }
        return s;
    }

    /**
     * 将指定字符串的首字母转换为小写，返回新的字符串
     *
     * @param s 字符串
     * @return 首字母小写的新字符串
     */
    public static String firstToLowerCase(String s) {
        if (StringUtils.isNotEmpty(s)) {
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                StringBuffer sb = new StringBuffer(s);
                sb.setCharAt(0, Character.toLowerCase(first));
                return sb.toString();
            }
        }
        return s;
    }

    /**
     * 校验指定字符串是否匹配指定通配符表达式。通配符表达式是指含有*和?的字符串，其中*代表匹配任意个字符，?代表匹配一个字符
     *
     * @param s       字符串
     * @param pattern 通配符表达式
     * @return true if 指定字符串匹配指定通配符表达式, otherwise false
     */
    public static boolean wildcardMatch(String s, String pattern) {
        // 先将通配符表达式转换为正则表达式
        pattern = pattern.replace('.', '#');
        pattern = pattern.replaceAll("#", "\\\\.");
        pattern = pattern.replace('*', '#');
        pattern = pattern.replaceAll("#", ".*");
        pattern = pattern.replace('?', '#');
        pattern = pattern.replaceAll("#", ".?");
        pattern = "^" + pattern + "$";
        // 按正则表达式校验匹配
        return regexMatch(s, pattern);
    }

    /**
     * 校验指定字符串是否匹配指定多个通配符表达式中的一个。<br/>
     * 通配符表达式是指含有*和?的字符串，其中*代表匹配任意个字符，?代表匹配一个字符
     *
     * @param s        字符串
     * @param patterns 通配符表达式清单
     * @return true if 指定字符串匹配指定多个通配符表达式中的一个, otherwise false
     */
    public static boolean wildcardMatchOneOf(String s, String... patterns) {
        for (String pattern : patterns) {
            if (wildcardMatch(s, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验指定字符串是否匹配指定多个通配符表达式中的一个。<br/>
     * 通配符表达式是指含有*和?的字符串，其中*代表匹配任意个字符，?代表匹配一个字符
     *
     * @param s        字符串
     * @param patterns 通配符表达式集合
     * @return true if 指定字符串匹配指定多个通配符表达式中的一个, otherwise false
     */
    public static boolean wildcardMatchOneOf(String s, Iterable<String> patterns) {
        if (patterns != null) {
            for (String pattern : patterns) {
                if (wildcardMatch(s, pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 校验指定字符串是否匹配指定ANT模式通配符表达式。<br/>
     * ANT模式通配符表达式是指含有**、*和?的字符串，其中**代表匹配任意级目录，*代表匹配任意个字符 ，?代表匹配任意一个字符
     *
     * @param s       字符串
     * @param pattern 通配符
     * @return true if 指定字符串匹配指定ANT模式通配符表达式, otherwise false
     */
    public static boolean antPathMatch(String s, String pattern) {
        return StringUtil.ANT_PATH_MATCHER.match(pattern, s);
    }

    /**
     * 校验指定字符串是否匹配指定多个ANT模式通配符表达式中的一个。<br/>
     * ANT模式通配符表达式是指含有**、*和?的字符串，其中**代表匹配任意级目录，*代表匹配任意个字符 ，?代表匹配任意一个字符
     *
     * @param s        字符串
     * @param patterns 通配符集合
     * @return true if 指定字符串匹配指定ANT模式通配符表达式, otherwise false
     */
    public static boolean antPathMatchOneOf(String s, String... patterns) {
        for (String pattern : patterns) {
            if (antPathMatch(s, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验指定字符串是否匹配指定多个ANT模式通配符表达式中的一个。<br/>
     * ANT模式通配符表达式是指含有**、*和?的字符串，其中**代表匹配任意级目录，*代表匹配任意个字符 ，?代表匹配任意一个字符
     *
     * @param s        字符串
     * @param patterns 通配符集合
     * @return true if 指定字符串匹配指定ANT模式通配符表达式, otherwise false
     */
    public static boolean antPathMatchOneOf(String s, Collection<String> patterns) {
        if (patterns != null) {
            for (String pattern : patterns) {
                if (antPathMatch(s, pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取指定字符串中以begin开始以end结束的所有子字符串
     *
     * @param s     字符串
     * @param begin 开始字符串
     * @param end   结束字符串
     * @return 子字符串集合
     */
    public static String[] substringsBetweens(String s, String begin, String end) {
        List<String> list = new ArrayList<>();
        if (begin.equals(end) && s.indexOf(begin) >= 0) {
            list.add(begin);
        }
        for (int index = s.indexOf(begin); index >= 0; index = s.indexOf(begin, index + 1)) {
            if (end.length() == 0) {
                list.add(s.substring(index));
            } else {
                int endIndex = s.indexOf(end, index + 1);
                if (endIndex > index) {
                    list.add(s.substring(index, endIndex + end.length()));
                } else if (endIndex < 0) {
                    break;
                }
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 截取指定字符串，限制其最大长度为指定长度。若最大长度小于0，则返回null
     *
     * @param s         字符串
     * @param maxLength 允许最大长度
     * @return 截取之后的字符串
     */
    public static String cut(String s, int maxLength) {
        if (s == null || maxLength < 0) {
            return null;
        }
        if (s.length() <= maxLength) {
            return s;
        } else {
            s = s.substring(0, maxLength);
            char[] chars = s.toCharArray();
            if (chars[chars.length - 1] > 255) {
                s = s.substring(0, s.length() - 1);
            } else if (s.length() >= 2) {
                s = s.substring(0, s.length() - 2);
            }
            return s + "...";
        }
    }

    /**
     * 判断是否数字字符
     *
     * @param c 字符
     * @return true if 是数字字符, otherwise false
     */
    public static boolean isNumberChar(char c) {
        byte b = (byte) c;
        return 48 <= b && b <= 57;
    }

    /**
     * 判断是否字母字符
     *
     * @param c 字符
     * @return true if 是字母字符, otherwise false
     */
    public static boolean isLetterChar(char c) {
        byte b = (byte) c;
        return 65 <= b && b <= 90 || 97 <= b && b <= 122;
    }

    /**
     * 判断是否标点符号字符
     *
     * @param c 字符
     * @return true if 是标点符号字符, otherwise false
     */
    public static boolean isPunctuationChar(char c) {
        byte b = (byte) c;
        return 33 <= b && b <= 47 || 58 <= b && b <= 64 || 91 <= b && b <= 96
                || 123 <= b && b <= 126;
    }

    /**
     * 判断字符串是否是邮件地址
     *
     * @param s 字符串
     * @return true if 是邮件地址, otherwise false
     */
    public static boolean isEmail(String s) {
        return s != null && regexMatch(s, StringUtil.EMAIL_PATTERN);
    }

    /**
     * 判断字符串是否是标准的URL
     *
     * @param s 字符串
     * @return true if 是标准URL，otherwise false
     */
    public static boolean isUrl(String s) {
        if (s == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(StringUtil.URL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 判断指定字符串是否为手机号码
     *
     * @param s 字符串
     * @return true if 指定字符串是手机号码, otherwise false
     */
    public static boolean isCellphone(String s) {
        return s != null && regexMatch(s, CELLPHONE_PATTERN);
    }

    /**
     * 判断指定字符串是否为中国大陆身份证号码
     *
     * @param s 字符串
     * @return true if 指定字符串是身份证号码, otherwise false
     */
    public static boolean isIdCardNo(String s) {
        return s != null && regexMatch(s, ID_CARD_NO_PATTERN);
    }

    /**
     * 判断指定字符串是否IP地址
     *
     * @param s 字符串
     * @return true if 指定字符串是IP地址, otherwise false
     */
    public static boolean isIp(String s) {
        return isIpv4(s) || isIpv6(s);
    }

    public static boolean isIpv4(String s) {
        return s != null && s.matches(StringUtil.IPv4_PATTERN);
    }

    public static boolean isIpv6(String s) {
        return s != null && s.matches(StringUtil.IPv6_PATTERN);
    }

    /**
     * 将指定字符串分割成指定长度的子串集，最后不足指定长度的子串仍计入结果集
     *
     * @param s      字符串
     * @param length 分割长度
     * @return 指定字符串按照指定长度分割出的子串集
     */
    public static String[] split(String s, int length) {
        if (s == null || length < 1) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        while (s.length() > length) {
            list.add(s.substring(0, length));
            s = s.substring(length);
        }
        list.add(s);
        return list.toArray(new String[0]);
    }

    public static <T> List<T> split(String s, String regex, Function<String, T> funcParseString) {
        List<T> list = new ArrayList<>();
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(regex);
            for (String str : array) {
                T obj = funcParseString.apply(str);
                if (obj != null) {
                    list.add(obj);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] split(String s, String regex, Function<String, T> funcParseString,
            Class<T> elementClass) {
        if (StringUtils.isNotBlank(s)) {
            String[] array = s.split(regex);
            T[] result = (T[]) Array.newInstance(elementClass, array.length);
            for (int i = 0; i < array.length; i++) {
                result[i] = funcParseString.apply(array[i]);
            }
            return result;
        }
        return (T[]) Array.newInstance(elementClass, 0);
    }

    public static String[] splitAndTrim(String s, String regex) {
        if (s == null) {
            return null;
        }
        String[] array = s.split(regex);
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }

    public static Set<String> splitToSet(String s, String regex, boolean trim) {
        if (s == null) {
            return null;
        }
        Set<String> set = new LinkedHashSet<>();
        String[] array = s.split(regex);
        for (String element : array) {
            if (trim) {
                element = element.trim();
            }
            set.add(element);
        }
        return set;
    }

    /**
     * 校验指定字符串是否采用指定字符集编码
     *
     * @param s           字符串
     * @param charsetName 字符集名称
     * @return true if 指定字符串采用指定字符集编码, otherwise false
     * @throws UnsupportedEncodingException 如果指定字符集编码不被支持
     */
    public static boolean validateCharset(String s, String charsetName)
            throws UnsupportedEncodingException {
        String s1 = new String(s.getBytes(), charsetName);
        return s1.equals(s);
    }

    /**
     * 按照标准格式解析指定字符串，转换为Map。标准格式即形如key1=value1,key2=value2,...
     *
     * @param s                          字符串
     * @param convertStaticPropertyValue 是否转换形如@truenewx.core.util.DateUtil@SHORT_DATE_PATTERN的静态属性值
     * @return 转换形成的Map
     */
    public static Map<String, String> toMapByStandard(String s,
            boolean convertStaticPropertyValue) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(s)) {
            String[] pairs = s.split(",");
            for (String pair : pairs) {
                int index = pair.indexOf('=');
                if (index > 0) {
                    String value = pair.substring(index + 1).trim();
                    if (convertStaticPropertyValue && value.startsWith("@")) {
                        value = (String) BeanUtil.getStaticPropertyExpressionValue(value);
                    }
                    map.put(pair.substring(0, index).trim(), value);
                }
            }
        }
        return map;
    }

    /**
     * 获取指定的基本名称、语言环境下的资源文件中的指定关键字对应的文本
     *
     * @param baseName 基本名称，是一个完全限定名，如：META-INF/i18n/message
     * @param locale   语言环境
     * @param key      文本关键字
     * @param args     文本替换参数
     * @return 指定的基本名称、语言环境下的资源文件中的指定关键字对应的文本
     * @throws MissingResourceException 如果未找到指定基本名称的资源文件
     */
    public static String getPropertiesText(String baseName, Locale locale, String key,
            String... args) {
        if (StringUtils.isEmpty(key)) {
            return key;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle rb = StringUtil.resourceBundleCache.get(baseName + "_" + locale);
        if (rb == null) {
            rb = ResourceBundle.getBundle(baseName, locale);
            StringUtil.resourceBundleCache.put(baseName + "_" + locale, rb);
        }
        try {
            String text = rb.getString(key);
            text = format(text, locale, args);
            return text;
        } catch (MissingResourceException e) {
            return key;
        } catch (RuntimeException e) {
            LogUtil.error(StringUtil.class, e);
        }
        return null;
    }

    /**
     * 按指定区域的风格格式化指定文本，并替换指定参数集合，返回格式化之后的文本
     *
     * @param text   文本
     * @param locale 区域
     * @param args   参数集
     * @return 格式化后的文本
     */
    public static String format(String text, Locale locale, String... args) {
        if (args != null && args.length > 0) {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            text = new MessageFormat(text, locale).format(args, new StringBuffer(), null)
                    .toString();
        }
        return text;
    }

    /**
     * 将指定字符串中的特殊字符转换为HTML代码，并返回新字符串
     *
     * @param s 字符串
     * @return 转换后的新字符串
     */
    public static String toHtml(String s) {
        // s = s.replaceAll(" ", "&nbsp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\n", "<br>");
        return s;
    }

    /**
     * 获取指定字符串包含的指定字符的个数
     *
     * @param s 字符串
     * @param c 字符
     * @return 指定字符串包含的指定字符的个数
     */
    public static int getCharCount(String s, char c) {
        char[] charArray = s.toCharArray();
        int count = 0;
        for (char ch : charArray) {
            if (ch == c) {
                count++;
            }
        }
        return count;
    }

    /**
     * 生成并返回一个去掉符号'-'之后的32位的UUID字符串
     *
     * @return 32位的UUID字符串
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 将形如zh_CN或en的字符串型区域转换为区域对象
     *
     * @param s 字符串型区域
     * @return 区域对象
     */
    public static Locale toLocale(String s) {
        if (StringUtils.isNotEmpty(s)) {
            try {
                String[] locales = s.split("_");
                if (locales.length == 1) {
                    return new Locale(locales[0]);
                } else if (locales.length == 2) {
                    return new Locale(locales[0], locales[1]);
                }
            } catch (Exception e) {
                // 忽略错误的区域格式
            }
        }
        return null;
    }

    /**
     * 替换指定字符串中的HTML标签为空格，返回替换后的结果
     *
     * @param s 字符串
     * @return 替换后的结果字符串
     */
    public static String replaceHtmlTagToSpace(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        String regex = "<[^>]+>"; // 定义HTML标签的正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        s = matcher.replaceAll(" ");
        return s;
    }

    /**
     * 除去右边指定的长度
     *
     * @param s      需要除去的字符串
     * @param length 需要除去的长度
     * @return 去除后的字符串
     */
    public static String rightCut(String s, int length) {
        if (StringUtils.isNotBlank(s)) {
            if (s.length() <= length) {
                return Strings.EMPTY;
            }
            s = s.substring(0, s.length() - length);
        }
        return s;
    }

    /**
     * 除去左边指定的长度
     *
     * @param s      需要除去的字符串
     * @param length 需要除去的长度
     * @return 去除后的字符串
     */
    public static String leftCut(String s, int length) {
        if (StringUtils.isNotBlank(s)) {
            if (s.length() <= length) {
                return Strings.EMPTY;
            }
            s = s.substring(length, s.length());
        }
        return s;
    }

    /**
     * 获取指定文件名的扩展名，如果指定文件名不是一个合法的文件名，则返回null
     *
     * @param fileName    文件名
     * @param containsDot 是否包含句点
     * @return 指定文件名的扩展名
     */
    public static String getFileExtension(String fileName, boolean containsDot) {
        if (fileName.length() == 0 && !containsDot) {
            return "";
        }
        if (".".equals(fileName)) {
            return containsDot ? "." : "";
        }
        int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            return containsDot ? fileName.substring(index) : fileName.substring(index + 1);
        }
        return null;
    }

    /**
     * 用指定分隔符连接指定对象数组的字符串值
     *
     * @param separator 分隔符
     * @param array     对象数组
     * @return 连接后的字符串
     */
    public static String join(String separator, Object... array) {
        return StringUtils.join(array, separator);
    }

    /**
     * 用指定分隔符连接指定整数数组的字符串值
     *
     * @param separator 分隔符
     * @param array     整数数组
     * @return 连接后的字符串
     */
    public static String join(String separator, int... array) {
        StringBuffer sb = new StringBuffer();
        for (int i : array) {
            sb.append(i).append(separator);
        }
        int length = sb.length();
        if (length > 0) {
            sb.delete(length - separator.length(), length);
        }
        return sb.toString();
    }

    /**
     * 用指定分隔符连接指定长整数数组的字符串值
     *
     * @param separator 分隔符
     * @param array     长整数数组
     * @return 连接后的字符串
     */
    public static String join(String separator, long... array) {
        StringBuffer sb = new StringBuffer();
        for (long i : array) {
            sb.append(i).append(separator);
        }
        int length = sb.length();
        if (length > 0) {
            sb.delete(length - separator.length(), length);
        }
        return sb.toString();
    }

    public static <T> String join(T[] array, String separator, Function<T, String> funcToString) {
        if (array == null) {
            return null;
        }
        if (funcToString == null) {
            return StringUtils.join(array, separator);
        }
        if (separator == null) {
            separator = Strings.EMPTY;
        }
        StringBuffer result = new StringBuffer();
        for (T obj : array) {
            String s = funcToString.apply(obj);
            if (s != null) {
                result.append(s).append(separator);
            }
        }
        if (result.length() > 0) {
            result.delete(result.length() - separator.length(), result.length());
        }
        return result.toString();
    }

    public static <T> String join(Iterable<T> iterable, String separator,
            Function<T, String> funcToString) {
        if (funcToString == null) {
            return StringUtils.join(iterable, separator);
        }
        if (separator == null) {
            separator = Strings.EMPTY;
        }
        StringBuffer result = new StringBuffer();
        for (T obj : iterable) {
            result.append(funcToString.apply(obj)).append(separator);
        }
        if (result.length() > 0) {
            result.delete(result.length() - separator.length(), result.length());
        }
        return result.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T parse(String s, Class<T> type) {
        if (type == String.class) {
            return (T) s;
        }
        if (type == StringBuffer.class) {
            return (T) new StringBuffer(s);
        }
        if (type == BigDecimal.class) {
            return (T) new BigDecimal(s);
        }
        if (type == BigInteger.class) {
            return (T) new BigInteger(s);
        }
        if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(s);
        }
        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(s);
        }
        if (type == Short.class || type == short.class) {
            return (T) Short.valueOf(s);
        }
        if (type == Byte.class || type == byte.class) {
            return (T) Byte.valueOf(s);
        }
        if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(s);
        }
        if (type == Float.class || type == float.class) {
            return (T) Float.valueOf(s);
        }
        if (Date.class.isAssignableFrom(type)) {
            return (T) DateUtil.parseLong(s);
        }
        if (type == Instant.class) {
            return (T) TemporalUtil.parseInstant(s);
        }
        if (type == LocalDate.class) {
            return (T) TemporalUtil.parseDate(s);
        }
        if (type == LocalTime.class) {
            return (T) TemporalUtil.parseTime(s);
        }
        if (type == LocalDateTime.class) {
            return (T) TemporalUtil.parseDateTime(s);
        }
        if (Currency.class.isAssignableFrom(type)) {
            return (T) Currency.getInstance(new Locale(s));
        }
        if (type.isEnum()) {
            return (T) EnumUtils.getEnum((Class<Enum>) type, s);
        }
        if (type.isArray()) {
            if (type.getComponentType() == String.class) {
                return (T) s.split(Strings.COMMA);
            }
            if (type.getComponentType() == Long.class) {
                return (T) MathUtil.parseLongObjectArray(s, Strings.COMMA);
            }
            if (type.getComponentType() == long.class) {
                return (T) MathUtil.parseLongArray(s, Strings.COMMA);
            }
            if (type.getComponentType() == Integer.class) {
                return (T) MathUtil.parseIntegerArray(s, Strings.COMMA);
            }
            if (type.getComponentType() == int.class) {
                return (T) MathUtil.parseIntArray(s, Strings.COMMA);
            }
        }
        return null;
    }

    public static String toPinyin(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        try {
            return PinyinHelper.convertToPinyinString(s, Strings.EMPTY, PinyinFormat.WITHOUT_TONE);
        } catch (PinyinException e) {
            return Strings.EMPTY;
        }
    }

    /**
     * 获取指定字符串的拼音缩写
     *
     * @param s 字符串
     * @return 拼音缩写
     */
    public static String toPinyinAbbr(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            String pinyin = toPinyin(String.valueOf(c));
            sb.append(pinyin.substring(0, 1));
        }
        return sb.toString();
    }

    public static boolean equalsIgnoreBlank(String s1, String s2) {
        // 两者均为空，则视为相等
        if (StringUtils.isBlank(s1) && StringUtils.isBlank(s2)) {
            return true;
        }
        return s1 != null && s1.equals(s2);
    }

    public static String replace(String s, int start, int length, char replacement) {
        if (s != null && start >= 0 && length > 0 && s.length() > start) {
            String prefix = s.substring(0, start);
            int index = start + length;
            String suffix = index >= s.length() ? Strings.EMPTY : s.substring(index);
            int size = s.length() - prefix.length() - suffix.length();
            s = prefix + String.valueOf(replacement).repeat(size) + suffix;
        }
        return s;
    }

    /**
     * 掩盖指定字符串的中间部分
     *
     * @param s 要掩盖的字符串
     * @return 掩盖后的字符串
     */
    public static String maskMiddle(String s) {
        if (s == null) {
            return null;
        }
        int start = s.length() / 3;
        int length = (s.length() - start) / 2;
        return replace(s, start, length, '*');
    }

    /**
     * 掩盖指定字符串的末尾部分
     *
     * @param s 要掩盖的字符串
     * @return 掩盖后的字符串
     */
    public static String maskEnd(String s) {
        if (s == null) {
            return null;
        }
        int start = (2 * s.length()) / 3;
        int length = s.length() - start;
        return replace(s, start, length, '*');
    }

    /**
     * 如果指定字符串为空，则返回指定默认值
     *
     * @param s            字符串
     * @param defaultValue 默认值
     * @return 处理后的结果
     */
    public static String ifBlank(String s, String defaultValue) {
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        return s;
    }

    /**
     * 如果指定字符串不为空，则执行指定消费者
     *
     * @param s        字符串
     * @param consumer 消费者
     */
    public static void ifNotBlank(String s, Consumer<String> consumer) {
        if (StringUtils.isNotBlank(s)) {
            consumer.accept(s);
        }
    }

    /**
     * 如果指定字符串不为空，则返回指定函数返回转换后的结果
     *
     * @param s        字符串
     * @param function 转换函数
     * @return 处理后的结果，指定字符串为空时返回null
     */
    public static <R> R ifNotBlank(String s, Function<String, R> function) {
        return ifNotBlankElse(s, function, null);
    }

    /**
     * 如果指定字符串不为空，则执行指定函数返回转换后的结果
     *
     * @param s             字符串
     * @param function      转换函数
     * @param defaultResult 指定字符串为空时的默认返回结果
     * @return 处理后的结果
     */
    public static <R> R ifNotBlankElse(String s, Function<String, R> function, R defaultResult) {
        if (StringUtils.isNotBlank(s)) {
            return function.apply(s);
        }
        return defaultResult;
    }

    /**
     * 在指定字符串的每个字符之间都插入指定字符串
     *
     * @param s           被插入的字符串
     * @param insertChars 插入的字符串
     * @param justified   字符串两端是否也都插入
     */
    public static String insertEach(String s, String insertChars, boolean justified) {
        if (s == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (justified) {
            result.append(insertChars);
        }
        char[] chars = s.toCharArray();
        for (char c : chars) {
            result.append(c).append(insertChars);
        }
        if (!justified) {
            result.delete(result.length() - insertChars.length(), result.length());
        }
        return result.toString();
    }

    /**
     * 用搜索关键字中的每一个字符依次进行匹配搜索
     *
     * @param content 搜索内容
     * @param keyword 搜索关键字
     * @return 是否匹配
     */
    public static boolean matchesForEach(String content, String keyword) {
        if (StringUtils.isBlank(content)) { // 搜索内容为空，则无法匹配
            return false;
        }
        if (StringUtils.isBlank(keyword)) { // 搜索关键字为空，则全部匹配
            return true;
        }
        int index = 0;
        for (char c : keyword.toCharArray()) {
            index = content.indexOf(c, index);
            if (index < 0) { // 搜索关键字中有一个字符未被包含在内容中，则不匹配
                return false;
            }
        }
        // 遍历后没有不匹配的字符，则说明整理匹配
        return true;
    }

    /**
     * 在指定字符串中依次查找指定搜索字符串集
     *
     * @param s                      字符串
     * @param searchStrings          搜索字符串集
     * @param plusSearchStringLength 返回结果位置是否加入匹配的搜索字符串的长度
     * @return 匹配的索引位置
     */
    public static int indexOfFirstInTurn(String s, String[] searchStrings, boolean plusSearchStringLength) {
        if (StringUtils.isNotBlank(s) && searchStrings != null) {
            for (String searchString : searchStrings) {
                if (StringUtils.isNotBlank(searchString)) {
                    int index = s.indexOf(searchString);
                    if (index >= 0) {
                        return plusSearchStringLength ? (index + searchString.length()) : index;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 在指定字符串中的每个大写字母前插入下划线
     *
     * @param s           字符串
     * @param toLowerCase 大写字母是否小写化
     * @return 插入后新的字符串
     */
    public static String prependUnderLineToUpperChar(String s, boolean toLowerCase) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        Matcher matcher = PATTERN_UPPER_CHAR.matcher(s);
        int index = 0;
        while (matcher.find()) {
            String group = matcher.group();
            if (toLowerCase) {
                group = group.toLowerCase();
            }
            sb.replace(matcher.start() + index, matcher.end() + index, Strings.UNDERLINE + group);
            index++;
        }
        if (sb.charAt(0) == '_') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * 获取指定字符串末尾包含的数字字符串
     *
     * @param s              字符串
     * @param acceptFraction 是否接受小数
     * @return 字符串末尾包含的数字字符串，如果不是以数字结尾，则返回空字符串
     */
    public static String getEndDecimalString(String s, boolean acceptFraction) {
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        // 反向遍历最大编号的每个字符，取得末尾的数值
        for (int i = chars.length - 1; i >= 0; i--) {
            char c = chars[i];
            if (isNumberChar(c)) {
                sb.insert(0, c);
            } else if (acceptFraction && c == '.') {
                sb.insert(0, c);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 获取指定字符串末尾包含的数值
     *
     * @param s              字符串
     * @param acceptFraction 是否接受小数
     * @return 字符串末尾包含的数值，如果不是以数字结尾，则返回null
     */
    public static BigDecimal getEndDecimal(String s, boolean acceptFraction) {
        try {
            String decimalString = getEndDecimalString(s, acceptFraction);
            if (decimalString.length() > 0) {
                return new BigDecimal(decimalString);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

}
