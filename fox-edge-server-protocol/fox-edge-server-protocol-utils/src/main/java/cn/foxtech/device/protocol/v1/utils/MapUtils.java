package cn.foxtech.device.protocol.v1.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多级MAP：用于简化多级MAP设置和查询操作，对于不存在的树干节点，自动补齐
 */
public class MapUtils {
    /**
     * 获取多级路径的数据
     *
     * @param map  哈希表
     * @param keys 参数列表，最后一个为缺省值
     * @return 指定路径上的数值
     * @throws ClassCastException 类型转换的异常
     */
    public static Object getOrDefault(Map map, Object... keys) throws ClassCastException {
        // 复制少一个末尾元素的副本
        Object[] newKeys = new Object[keys.length - 1];
        for (int i = 0; i < newKeys.length; i++) {
            newKeys[i] = keys[i];
        }

        // 获得map中的数值
        Object value = getValue(map, newKeys);

        // 如果是没有，那么用传递参数的末尾元素作为缺省值
        if (value == null) {
            return keys[keys.length - 1];
        }

        // 否则返回数值
        return value;
    }

    /**
     * 获得指定路径上的数值
     *
     * @param map   哈希表
     * @param clazz 待返回的value的数据类型，最后一个可变参数为缺省值
     * @param keys  路径
     * @param <T>   数据类型
     * @return 返回值
     * @throws ClassCastException 类型转换异常
     */
    public static <T> T getOrDefault(Map map, Class<T> clazz, Object... keys) throws ClassCastException {
        // 复制少一个末尾元素的副本
        Object[] newKeys = new Object[keys.length - 1];
        for (int i = 0; i < newKeys.length; i++) {
            newKeys[i] = keys[i];
        }

        // 获得map中的数值
        T value = getValue(map, clazz, newKeys);

        // 如果是没有，那么用传递参数的末尾元素作为缺省值
        if (value == null) {
            return (T) keys[keys.length - 1];
        }

        // 否则返回数值
        return value;
    }

    /**
     * 获得指定路径上的数值
     *
     * @param map   哈希表
     * @param clazz 待返回的value的数据类型
     * @param keys  路径
     * @param <T>   数据类型
     * @return 返回值
     * @throws ClassCastException 类型转换异常
     */
    public static <T> T getValue(Map map, Class<T> clazz, Object... keys) throws ClassCastException {
        // 获得map中的数值
        Object value = getValue(map, keys);

        // 如果是没有，那么用传递参数的末尾元素作为缺省值
        if (value == null) {
            return null;
        }

        // 类型一致性检查
        if (!clazz.isInstance(value)) {
            return null;
        }

        // 否则返回数值
        return (T) value;
    }


    /**
     * 获取多级路径的数据
     *
     * @param map  Map
     * @param keys 各级的Key
     * @return 返回的数值
     * @throws ClassCastException 类型转换异常
     */
    public static Object getValue(Map map, Object... keys) throws ClassCastException {
        Map self = map;

        for (int i = 0; i < keys.length - 1; i++) {
            Object key = keys[i];

            // 初始化子节点
            Object child = self.get(key);
            if (child == null) {
                return null;
            }

            // 检查：此时是否为map结构
            if (!(child instanceof Map)) {
                return null;
            }

            // 切换到下一级
            self = (Map) child;
        }

        return self.get(keys[keys.length - 1]);
    }

    /**
     * 设置多级数据
     *
     * @param map  Map
     * @param keys 各级key
     * @throws ClassCastException 转换异常
     */
    public static void setValue(Map map, Object... keys) throws ClassCastException {
        Map self = map;

        for (int i = 0; i < keys.length - 2; i++) {
            Object key = keys[i];

            // 初始化子节点
            Map child = (Map) self.get(key);
            if (child == null) {
                if (map instanceof HashMap) {
                    child = new HashMap();
                } else if (map instanceof ConcurrentHashMap) {
                    child = new ConcurrentHashMap();
                } else {
                    throw new ClassCastException("只支持:HashMap和ConcurrentHashMap，而输入的是" + map.getClass().getSimpleName());
                }

                self.put(key, child);
            }
            // 切换到下一级
            self = child;
        }

        self.put(keys[keys.length - 2], keys[keys.length - 1]);
    }

    public static void remove(Map map, Object... keys) throws ClassCastException {
        Map self = map;

        for (int i = 0; i < keys.length - 1; i++) {
            Object key = keys[i];

            // 初始化子节点
            Map child = (Map) self.get(key);
            if (child == null) {
                return;
            }
            // 切换到下一级
            self = child;
        }

        self.remove(keys[keys.length - 1]);
    }

    public static void setValue(Map map, Object[] path, Object value) throws ClassCastException {
        Map self = map;

        for (int i = 0; i < path.length; i++) {
            Object key = path[i];
            Object child = self.get(key);

            if (i < path.length - 1) {
                // 如果为空，那么预创建该Map节点
                if (child == null) {
                    if (map instanceof HashMap) {
                        child = new HashMap();
                    } else if (map instanceof ConcurrentHashMap) {
                        child = new ConcurrentHashMap();
                    } else {
                        throw new ClassCastException("只支持:HashMap和ConcurrentHashMap，而输入的是" + map.getClass().getSimpleName());
                    }

                    self.put(key, child);
                }

                // 切换节点
                self = (Map) child;
                continue;
            }


            self.put(key, value);
        }
    }

    public static void setDefaultValue(Map map, Object key, Object defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            map.put(key, defaultValue);
            return;
        }
    }

    public static void setDefaultValue(Map map, Object key, Class type, Object defaultValue) {
        Object value = map.get(key);
        if (value == null || !type.isInstance(value)) {
            map.put(key, defaultValue);
            return;
        }
    }

    public static void copyValue(Map<String, Object> data, String keyData, Map<String, Object> param, String keyParam, Object defaultValue) {
        Object value = param.get(keyParam);
        if (value == null) {
            value = defaultValue;
        }

        data.put(keyData, value);
    }

    /**
     * 将一种Map转换成另一种Map，例如将HashMap转换未TreeMap
     *
     * @param map
     * @param mapType
     * @return
     * @throws ClassCastException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Map castMap(Map map, Class mapType) throws ClassCastException, InstantiationException, IllegalAccessException {
        if (!Map.class.isAssignableFrom(mapType)) {
            throw new ClassCastException();
        }

        Map node = (Map) mapType.newInstance();

        for (Object key : map.keySet()) {
            Object value = map.get(key);

            if (value instanceof Map) {
                node.put(key, castMap((Map) value, mapType));
            }
            else if (value instanceof Collection) {
                Collection list = (Collection)value.getClass().newInstance();

                Collection vs = (Collection)value;
                for (Object v : vs){
                    if (v instanceof Map){
                        list.add(castMap((Map)v,mapType));
                    }
                    else {
                        list.add(v);
                    }
                }

                node.put(key, list);
            } else {
                node.put(key, value);
            }
        }

        return node;
    }

    public static List castMap(List mapList, Class mapType) throws ClassCastException, InstantiationException, IllegalAccessException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object map : mapList) {
            result.add(castMap((Map)map, mapType));
        }

        return result;
    }
}
