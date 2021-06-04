package org.truenewx.tnxjee.model.entity.unity;

import java.io.Serializable;
import java.util.*;

/**
 * 单体工具类
 *
 * @author jianglei
 */
public class UnityUtil {

    private UnityUtil() {
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

}
