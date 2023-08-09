package cn.foxtech.common.utils.bean;

import org.springframework.cglib.beans.BeanMap;

import java.util.*;

/**
 * BeanMap反射工具
 */
public class BeanMapUtils {
    /**
     * map转java对象
     *
     * @param map Map对象
     * @param beanClass 待构造的对象类型
     * @return 对象
     * @throws Exception 转换异常
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        Object object = beanClass.newInstance();
        BeanMap beanMap = BeanMap.create(object);
        beanMap.putAll(map);
        return object;
    }

    /**
     * 将对象转换为Map
     * @param obj 对象
     * @return Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj != null) {
            BeanMap beanMap = BeanMap.create(obj);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 生成的Map后，剔除指定的key
     *
     * @param objectList 对象
     * @param filterKeys 要剔除的Key
     * @param <T> 类型名
     * @return map
     */
    public static <T> List<Map<String, Object>> objectToMap(Collection<T> objectList, List<String> filterKeys) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T entity : objectList) {
            Map<String, Object> map = BeanMapUtils.objectToMap(entity);

            for (String filter : filterKeys) {
                map.remove(filter);
            }

            result.add(map);
        }

        return result;
    }

    /**
     * 生成的Map后，剔除指定的key
     *
     * @param object     对象
     * @param filterKeys 要剔除的Key
     * @param <T>
     * @return map
     */
    public static <T> Map<String, Object> objectToMap(T object, List<String> filterKeys) {
        Map<String, Object> map = BeanMapUtils.objectToMap(object);

        for (String filter : filterKeys) {
            map.remove(filter);
        }

        return map;
    }
}
