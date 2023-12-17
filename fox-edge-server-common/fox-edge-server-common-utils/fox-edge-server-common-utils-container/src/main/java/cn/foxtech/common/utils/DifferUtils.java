package cn.foxtech.common.utils;

import java.util.*;

/**
 * 比较工具
 *
 * @author h00442047
 * @since 2019年12月28日
 */
public class DifferUtils {
    /**
     * 以A为基准进行比较：B即转换为A，需要进行的增删操作
     *
     * @param srcList 基准列表
     * @param dstList 比对对象
     * @param addList 比对对象相对基准表，需要增加的数据部分
     * @param delList 比对对象相对基准表，需要减少的数据部分
     * @param eqlList 比对对象相对基准表，不发生变化的数据部分
     * @param <T> 数据类型
     */
    public static <T> void differByValue(Collection<T> srcList, Collection<T> dstList, Collection<T> addList, Collection<T> delList, Collection<T> eqlList) {
        differA2BByValue(srcList, dstList, addList, eqlList);
        differA2BByValue(dstList, srcList, delList, eqlList);
    }

    /**
     * 以A为基准进行比较
     *
     * @param srcList 基准列表
     * @param dstList 比对对象
     * @param addList 比对对象相对基准表，需要增加的数据部分
     * @param eqlList 比对对象相对基准表，已经存在的数据部分
     */
    private static <T> void differA2BByValue(Collection<T> srcList, Collection<T> dstList, Collection<T> addList, Collection<T> eqlList) {
        addList.clear();
        eqlList.clear();

        for (T aT : dstList) {
            // 检查：是否存在
            boolean isexist = false;
            for (T bT : srcList) {
                if (aT.equals(bT)) {
                    isexist = true;
                    break;
                }
            }

            if (!isexist) {
                addList.add(aT);
            } else {
                eqlList.add(aT);
            }
        }
    }

    /**
     * 比较A/B是否数值完全相同
     *
     * @param aList a集合
     * @param bList b集合
     * @return 是否一致
     * @param <T> 数据类型
     */
    public static <T> boolean differByValue(Set<T> aList, Set<T> bList) {
        Set<T> addList = new HashSet<T>();
        Set<T> delList = new HashSet<T>();
        Set<T> eqlList = new HashSet<T>();
        differByValue(aList, bList, addList, delList, eqlList);

        return !addList.isEmpty() || !delList.isEmpty();
    }

    /**
     * 以DST为基准进行比较：SRC即转换为DST，对SRC需要进行的增删操作
     *
     * @param srcList 源数据
     * @param dstList 目标数据
     * @param addList 新增的数据
     * @param delList 删除的数据
     * @param eqlList 相同的数据
     * @param <T> 数据类型
     */
    public static <T> void differByValue(Set<T> srcList, Set<T> dstList, Set<T> addList, Set<T> delList, Set<T> eqlList) {
        differA2BByValue(srcList, dstList, addList, eqlList);
        differA2BByValue(dstList, srcList, delList, eqlList);
    }

    private static <T> void differA2BByValue(Set<T> srcList, Set<T> dstList, Set<T> addList, Set<T> eqlList) {
        for (T dstT : dstList) {
            if (null != dstT && srcList.contains(dstT)) {
                eqlList.add(dstT);
            } else {
                addList.add(dstT);
            }
        }
    }

    /**
     * 比较两个列表是否完全一致：不但比较A/B列表的数值一致，还比较双方数值顺序的一致性
     *
     * @param aList 源数据
     * @param bList 目的数据
     * @return 是否一致
     * @param <T> 数据类型
     */
    public static <T> boolean differA2BByConsistency(List<T> aList, List<T> bList) {
        int size = aList.size();
        if (bList.size() != size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!aList.get(i).equals(bList.get(i))) {
                return false;
            }
        }

        return true;
    }
}
