package org.truenewx.tnxjee.model.entity.util;

import java.io.Serializable;
import java.util.*;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.model.entity.unity.Unity;

/**
 * 实体工具类
 *
 * @author jianglei
 */
public class EntityUtil {

    private EntityUtil() {
    }

    /**
     * 获取指定单体集合的id集合
     *
     * @param unities 单体集合
     * @return id集合
     */
    public static <K extends Serializable> Set<K> getIdSet(Collection<? extends Unity<K>> unities) {
        Set<K> ids = new LinkedHashSet<>(); // 保持顺序
        for (Unity<K> unity : unities) {
            ids.add(unity.getId());
        }
        return ids;
    }

    /**
     * 获取指定单体集合的整型id数组
     *
     * @param unities 单体集合
     * @return id数组
     */
    public static int[] getIntIdArray(Collection<? extends Unity<Integer>> unities) {
        int[] idArray = new int[unities.size()];
        int i = 0;
        for (Unity<Integer> unity : unities) {
            idArray[i++] = unity.getId();
        }
        return idArray;
    }

    /**
     * 获取指定单体集合的长整型id数组
     *
     * @param unities 单体集合
     * @return id数组
     */
    public static long[] getLongIdArray(Collection<? extends Unity<Long>> unities) {
        long[] idArray = new long[unities.size()];
        int i = 0;
        for (Unity<Long> unity : unities) {
            idArray[i++] = unity.getId();
        }
        return idArray;
    }

    /**
     * 获取指定单体集合的字符串id数组
     *
     * @param unities 单体集合
     * @return id数组
     */
    public static String[] getStringIdArray(Collection<? extends Unity<String>> unities) {
        String[] idArray = new String[unities.size()];
        int i = 0;
        for (Unity<String> unity : unities) {
            idArray[i++] = unity.getId();
        }
        return idArray;
    }

    /**
     * 判断指定单体集合中是否包含指定id的单体
     *
     * @param unities 单体集合
     * @param id      单体id
     * @return 指定单体集合中是否包含指定id的单体
     */
    public static <T extends Unity<K>, K extends Serializable> boolean containsId(
            Collection<T> unities, K id) {
        return indexOfId(unities, id) >= 0;
    }

    /**
     * 获取指定单体id在指定单体集合中的位置索引下标
     *
     * @param unities 单体集合
     * @param id      单体id
     * @return 指定单体id在指定单体集合中的位置索引下标，如果没找到则返回-1
     */
    public static <T extends Unity<K>, K extends Serializable> int indexOfId(Collection<T> unities,
            K id) {
        if (unities != null) {
            int i = 0;
            for (Unity<K> unity : unities) {
                if (unity.getId().equals(id)) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    /**
     * 将指定单体集合转换为以id为key，单体对象为value的Map映射集
     *
     * @param unities 单体集合
     * @return 单体映射集
     */
    public static <T extends Unity<K>, K extends Serializable> Map<K, T> toMap(
            Collection<T> unities) {
        if (unities == null) {
            return null;
        }
        Map<K, T> map = new HashMap<>();
        for (T unity : unities) {
            map.put(unity.getId(), unity);
        }
        return map;
    }

    public static <T extends Unity<K>, K extends Serializable> T getById(Collection<T> unities, K id) {
        if (unities != null && id != null) {
            for (T unity : unities) {
                if (id.equals(unity.getId())) {
                    return unity;
                }
            }
        }
        return null;
    }

    /**
     * 将指定对象转换为类JSON格式，与JSON的差别在于以,作为首尾，而不是{}或[]
     *
     * @param bean              对象
     * @param ignoredProperties 忽略的属性名称集
     * @return 类JSON
     */
    public static String toJsonLike(Object bean, String... ignoredProperties) {
        String json = JsonUtil.toJson(bean, ignoredProperties);
        if (json != null) {
            if ((json.startsWith(Strings.LEFT_BRACE) && json.endsWith(Strings.RIGHT_BRACE))
                    || (json.startsWith(Strings.LEFT_SQUARE_BRACKET) && json.endsWith(Strings.RIGHT_SQUARE_BRACKET))) {
                return Strings.COMMA + json.substring(1, json.length() - 1) + Strings.COMMA;
            }
        }
        return json;
    }

    /**
     * 解析类JSON格式字符串为映射集
     *
     * @param json 类JSON字符串，与JSON的差别在于以,作为首尾，而不是{}或[]
     * @return 映射集
     */
    public static Map<String, Object> parseJsonLike(String json) {
        if (json != null && json.startsWith(Strings.COMMA) && json.endsWith(Strings.COMMA)) {
            json = Strings.LEFT_BRACE + json.substring(1, json.length() - 1) + Strings.RIGHT_BRACE;
        }
        return JsonUtil.json2Map(json);
    }

    /**
     * 解析类JSON格式字符串为指定类型的对象
     *
     * @param json 类JSON字符串，与JSON的差别在于以,作为首尾，而不是{}或[]
     * @param type 结果对象类型
     * @param <T>  结果对象类型
     * @return 结果对象
     */
    public static <T> T parseJsonLike(String json, Class<T> type) {
        if (json != null && json.startsWith(Strings.COMMA) && json.endsWith(Strings.COMMA)) {
            if (type.isArray() || Collection.class.isAssignableFrom(type)) {
                json = Strings.LEFT_SQUARE_BRACKET + json.substring(1,
                        json.length() - 1) + Strings.RIGHT_SQUARE_BRACKET;
            } else {
                json = Strings.LEFT_BRACE + json.substring(1, json.length() - 1) + Strings.RIGHT_BRACE;
            }
        }
        return JsonUtil.json2Bean(json, type);
    }

}
