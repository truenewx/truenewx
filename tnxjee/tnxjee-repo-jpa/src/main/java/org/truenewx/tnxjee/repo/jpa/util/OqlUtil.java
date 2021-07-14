package org.truenewx.tnxjee.repo.jpa.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ArrayUtil;
import org.truenewx.tnxjee.model.query.FieldOrder;
import org.truenewx.tnxjee.model.query.Querying;

/**
 * 对象查询语言(OQL)工具类
 *
 * @author jianglei
 */
public class OqlUtil {

    private OqlUtil() {
    }

    /**
     * 根据指定查询排序序列构建排序语句，以 order by 开头<br/>
     * 如果无排序设置，则返回空字符串
     *
     * @param orders 查询排序序列
     * @return 排序语句
     */
    public static String buildOrderString(FieldOrder... orders) {
        return buildOrderString(Arrays.asList(orders));
    }

    /**
     * 根据指定查询排序序列构建排序语句，以 order by 开头<br/>
     * 如果无排序设置，则返回空字符串
     *
     * @param orders 查询排序序列
     * @return 排序语句
     */
    public static String buildOrderString(Collection<FieldOrder> orders) {
        String orderString = Querying.toOrderBy(orders);
        if (orderString.length() > 0) {
            orderString = " order by " + orderString;
        }
        return orderString;
    }

    /**
     * 构建OR条件子句
     *
     * @param params           查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName        字段名
     * @param fieldParamValues 字段参数值
     * @param comparison       条件比较符
     * @return OR条件子句
     * @author jianglei
     */
    public static String buildOrConditionString(Map<String, Object> params, String fieldName,
            Collection<?> fieldParamValues, Comparison comparison) {
        StringBuilder condition = new StringBuilder();
        if (fieldParamValues != null && fieldParamValues.size() > 0) {
            if (comparison == null) { // 默认为等于比较符
                comparison = Comparison.EQUAL;
            }
            // 等于和不等于在参数个数大于5后使用IN/NOT IN代替
            if ((comparison == Comparison.EQUAL || comparison == Comparison.NOT_EQUAL) && fieldParamValues.size() > 5) {
                condition.append(fieldName);
                if (comparison == Comparison.EQUAL) {
                    condition.append(Comparison.IN.toQlString());
                } else {
                    condition.append(Comparison.NOT_IN.toQlString());
                }
                String paramName = getParamName(fieldName);
                condition.append(Strings.LEFT_BRACKET).append(Strings.COLON).append(paramName)
                        .append(Strings.RIGHT_BRACKET);
                params.put(paramName, fieldParamValues);
            } else {
                String junction = comparison.isNot() ? QlConstants.JUNCTION_AND : QlConstants.JUNCTION_OR;
                int i = 0;
                for (Object fieldParamValue : fieldParamValues) {
                    condition.append(junction).append(fieldName);
                    if (fieldParamValue != null) { // 忽略为null的参数值
                        String paramName = getParamName(fieldName) + (i++);
                        condition.append(comparison.toQlString()).append(Strings.COLON).append(paramName);
                        if (comparison == Comparison.LIKE || comparison == Comparison.NOT_LIKE) {
                            params.put(paramName,
                                    StringUtils.join(Strings.PERCENT, fieldParamValue.toString(), Strings.PERCENT));
                        } else if (comparison == Comparison.LIKE_LEFT || comparison == Comparison.NOT_LIKE_LEFT) {
                            params.put(paramName, StringUtils.join(Strings.PERCENT, fieldParamValue.toString()));
                        } else if (comparison == Comparison.LIKE_RIGHT || comparison == Comparison.NOT_LIKE_RIGHT) {
                            params.put(paramName, StringUtils.join(fieldParamValue.toString(), Strings.PERCENT));
                        } else {
                            params.put(paramName, fieldParamValue);
                        }
                    }
                }
                if (fieldParamValues.size() == 1) { // 一个字段参数不需要添加括号
                    condition.delete(0, junction.length());
                } else {
                    condition.replace(0, junction.length(), Strings.LEFT_BRACKET)
                            .append(Strings.RIGHT_BRACKET); // 去掉多余的or后添加括号
                }
            }
        }
        return condition.toString();
    }

    private static String getParamName(String fieldName) {
        return fieldName.replaceAll("\\.", Strings.UNDERLINE);
    }

    /**
     * 构建OR条件子句
     *
     * @param params           查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName        字段名
     * @param fieldParamValues 字段参数值
     * @param comparison       条件比较符
     * @return OR条件子句
     * @author jianglei
     */
    public static String buildOrConditionString(Map<String, Object> params, String fieldName, Object[] fieldParamValues,
            Comparison comparison) {
        return buildOrConditionString(params, fieldName, Arrays.asList(fieldParamValues), comparison);
    }

    public static String buildOrConditionString(Map<String, Object> params, String fieldName, int[] fieldParamValues,
            Comparison comparison) {
        return buildOrConditionString(params, fieldName, ArrayUtil.toList(fieldParamValues), comparison);
    }

    public static String buildOrConditionString(Map<String, Object> params, String fieldName, long[] fieldParamValues,
            Comparison comparison) {
        return buildOrConditionString(params, fieldName, ArrayUtil.toList(fieldParamValues), comparison);
    }

    /**
     * 构建指定字段的为null条件子句
     *
     * @param fieldName 字段名
     * @param ifNull    是否为null，其值本身为null表示忽略该字段条件
     * @return 条件子句
     */
    public static String buildNullConditionString(String fieldName, Boolean ifNull) {
        StringBuilder condition = new StringBuilder();
        if (ifNull != null) {
            condition.append(fieldName).append(QlConstants.KEYWORD_IS);
            if (!ifNull) {
                condition.append(QlConstants.KEYWORD_NOT);
            }
            condition.append(QlConstants.KEYWORD_NULL);
        }
        return condition.toString();
    }

    /**
     * 构建指定字段的不为null条件子句
     *
     * @param fieldName 字段名
     * @param ifNotNull 是否不为null，其值本身为null表示忽略该字段条件
     * @return 条件子句
     */
    public static String buildNotNullConditionString(String fieldName, Boolean ifNotNull) {
        StringBuilder condition = new StringBuilder();
        if (ifNotNull != null) {
            condition.append(fieldName).append(QlConstants.KEYWORD_IS);
            if (ifNotNull) {
                condition.append(QlConstants.KEYWORD_NOT);
            }
            condition.append(QlConstants.KEYWORD_NULL);
        }
        return condition.toString();
    }

    /**
     * 构建索引字段Like条件子句
     *
     * @param params          查询参数映射集，相关查询参数会写入该映射集中
     * @param fieldName       索引字段名
     * @param fieldParamValue 索引字段参数值，应该为汉语拼音
     * @return 索引字段Like条件子句
     */
    public static String buildIndexLikeConditionString(Map<String, Object> params, String fieldName,
            String fieldParamValue) {
        StringBuilder condition = new StringBuilder();
        if (StringUtils.isNotBlank(fieldParamValue)) {
            condition.append(fieldName).append(Comparison.LIKE.toQlString()).append(Strings.COLON);
            String paramName = getParamName(fieldName);
            condition.append(paramName);
            StringBuilder likeValue = new StringBuilder(Strings.PERCENT);
            char[] chars = fieldParamValue.toCharArray();
            for (char c : chars) {
                likeValue.append(c).append(Strings.PERCENT);
            }
            params.put(paramName, likeValue.toString());
        }
        return condition.toString();
    }

    public static String appendAndCondition(String ql, String condition) {
        if (StringUtils.isNotBlank(condition)) {
            ql += QlConstants.JUNCTION_AND + condition;
        }
        return ql;
    }

    public static void appendAndCondition(StringBuilder ql, String condition) {
        if (StringUtils.isNotBlank(condition)) {
            ql.append(QlConstants.JUNCTION_AND).append(condition);
        }
    }
}
