/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 集合操作的工具类
 */
public class ContainerUtils {
    public ContainerUtils() {
    }

    /**
     * 获取类函数的名称
     * 例如：getMethodName(Integer.toHexString)，返回的就是toHexString
     *
     * @param function
     * @param <E>
     * @param <R>
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E, R> String getMethodName(SerializableFunction<E, R> function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = function.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
        String implMethodName = serializedLambda.getImplMethodName();

        return implMethodName;
    }

    /**
     * 获取类的函数
     *
     * @param clazz
     * @param function
     * @param <E>
     * @param <R>
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E, R, T> Method getMethod(Class<T> clazz, SerializableFunction<E, R> function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String methodName = ContainerUtils.getMethodName(function);
        return clazz.getMethod(methodName);
    }

    /**
     * 根据对象列表中的对象的getXxxx()函数，取出成员
     *
     * @param objList
     * @param clazz
     * @param method  method是clazz的成员函数
     * @param <K>
     * @param <T>
     * @return
     */
    private static <K, T> List<K> buildListByGetField(List<T> objList, Class<K> clazz, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<K> keyList = new ArrayList<K>();
        for (T obj : objList) {
            // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
            Object keyObject = method.invoke(obj);
            if (clazz.isInstance(keyObject)) {
                K key = clazz.cast(keyObject);
                keyList.add(key);
            }
        }

        return keyList;
    }

    /**
     * 根据对象的getXxxx()，取出类型列表中的数据
     *
     * @param objList  AClass对象列表
     * @param clazz    TClass AClass::getXxxx()中，TClass这样的成员
     * @param function AClass::getXxxx 这样的函数
     * @param <E>      类的类型
     * @param <R>      字段
     * @param <K>      字段的类型
     * @param <T>      对象的类型
     * @return 对象列表
     */
    public static <E, R, K, T> List<K> buildListByGetField(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new ArrayList<K>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildListByGetField(objList, clazz, method);
        } catch (NoSuchMethodException e) {
            return new ArrayList<K>();
        } catch (SecurityException e) {
            return new ArrayList<K>();
        } catch (IllegalAccessException e) {
            return new ArrayList<K>();
        } catch (IllegalArgumentException e) {
            return new ArrayList<K>();
        } catch (InvocationTargetException e) {
            return new ArrayList<K>();
        }
    }

    /**
     * 根据Key生成Map，该方法是是具体类的函数，（不具备多态能力，不是反射，速度很快）
     *
     * @param objList 对象列表
     * @param clazz   类型名称
     * @param method  使用obj.getClass().getMethod("getTnlKey", new Class[0])获取Method
     * @param <K>     key类型
     * @param <T>     对象类型
     * @return Map结构的列表
     */
    public static <K, T> Map<K, T> buildMapByKeyAndFinalMethod(List<T> objList, Class<K> clazz, Method method) {
        try {
            Map<K, T> uid2deviceMap = new HashMap<K, T>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                if (clazz.isInstance(keyObject)) {
                    K key = clazz.cast(keyObject);
                    uid2deviceMap.put(key, obj);
                }
            }

            return uid2deviceMap;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }


    /**
     * 根据指定的Key转换为Map结构
     *
     * @param objList  对象列表
     * @param function Key所在的字段
     * @param <E>      对象类型
     * @param <R>      对象的字段
     * @param <K>      key的字段
     * @param <T>      对象的类型
     * @return Map
     */
    public static <E, R, K, T> Map<K, T> buildMapByKey(List<T> objList, SerializableFunction<E, R> function) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByKey(objList, method);
        } catch (NoSuchMethodException e) {
            return new HashMap<K, T>();
        } catch (SecurityException e) {
            return new HashMap<K, T>();
        } catch (IllegalAccessException e) {
            return new HashMap<K, T>();
        } catch (IllegalArgumentException e) {
            return new HashMap<K, T>();
        } catch (InvocationTargetException e) {
            return new HashMap<K, T>();
        }
    }


    /**
     * 根据Key字段构造数据
     *
     * @param objList 对象列表
     * @param method  字段
     * @param <K>     Key的类型
     * @param <T>     对象类型
     * @return Map
     */
    public static <K, T> Map<K, T> buildMapByKey(List<T> objList, Method method) {
        try {
            Map<K, T> uid2deviceMap = new HashMap<K, T>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                uid2deviceMap.put((K) keyObject, obj);
            }

            return uid2deviceMap;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 根据Key生成Map
     *
     * @param mapList map列表数据
     * @param key     map中的字段
     * @param clazz   key字段对应的object的数值类型
     * @param <V>     Key的类型
     * @return Map
     */
    public static <V> Map<V, Map<String, Object>> buildMapByKey(List<Map<String, Object>> mapList, String key, Class<V> clazz) {
        Map<V, Map<String, Object>> result = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            V object = (V) map.get(key);
            if (object == null) {
                continue;
            }

            result.put(object, map);
        }

        return result;
    }

    /**
     * 分类
     *
     * @param objList 对象列表
     * @param clazz   类型
     * @param method  方法
     * @param <K>     key类型
     * @param <T>     对象类型
     * @return Map
     */
    public static <K, T> Map<K, List<T>> buildMapByTypeAndFinalMethod(List<T> objList, Class<K> clazz, Method method) {
        try {
            Map<K, List<T>> uid2deviceMap = new HashMap<>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                if (clazz.isInstance(keyObject)) {
                    K key = clazz.cast(keyObject);

                    List<T> list = uid2deviceMap.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                        uid2deviceMap.put(key, list);
                    }

                    list.add(obj);
                }
            }

            return uid2deviceMap;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 根据Key生成Map，该方法是是具体类的函数，（不具备多态能力，不是反射，速度很快）
     *
     * @param objList  类型AClass的列表容器
     * @param function 类型AClass中的某个成员getxxxx()
     * @param clazz    类型AClass中的某个成员BClass getxxxx()中的BClass
     * @param <E>      对象类型
     * @param <R>      字段
     * @param <K>      key类型
     * @param <T>      数据类型
     * @return Map
     */
    public static <E, R, K, T> Map<K, T> buildMapByKeyAndFinalMethod(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByKeyAndFinalMethod(objList, clazz, method);
        } catch (NoSuchMethodException e) {
            return new HashMap<K, T>();
        } catch (SecurityException e) {
            return new HashMap<K, T>();
        } catch (IllegalAccessException e) {
            return new HashMap<K, T>();
        } catch (IllegalArgumentException e) {
            return new HashMap<K, T>();
        } catch (InvocationTargetException e) {
            return new HashMap<K, T>();
        }
    }

    /**
     * 根据方法组织Map
     *
     * @param objList  对象列表
     * @param function 方法
     * @param clazz    类型
     * @param <E>      对象类型
     * @param <R>      字段
     * @param <K>      Key类型
     * @param <T>      数据类型
     * @return Map
     */
    public static <E, R, K, T> Map<K, List<T>> buildMapByTypeAndFinalMethod(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByTypeAndFinalMethod(objList, clazz, method);
        } catch (NoSuchMethodException e) {
            return new HashMap<K, List<T>>();
        } catch (SecurityException e) {
            return new HashMap<K, List<T>>();
        } catch (IllegalAccessException e) {
            return new HashMap<K, List<T>>();
        } catch (IllegalArgumentException e) {
            return new HashMap<K, List<T>>();
        } catch (InvocationTargetException e) {
            return new HashMap<K, List<T>>();
        }
    }

    /**
     * 根据map中的某个元素，将列表转换成以这个元素为key的map
     *
     * @param objList 对象类型
     * @param mapKey  key
     * @param clazz   类型
     * @param <K>     key类型
     * @param <T>     数据类型
     * @return Map
     */
    public static <K, T> Map<K, Map<String, Object>> buildMapByMapAt(List<Map<String, Object>> objList, String mapKey, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        Map<K, Map<String, Object>> result = new HashMap<>();
        for (Map<String, Object> obj : objList) {
            Object value = obj.get(mapKey);
            if (!clazz.isInstance(value)) {
                continue;
            }

            result.put((K) value, obj);
        }

        return result;
    }

    /**
     * 从A类型列表转换成B类型列表:A/B是派生类关系
     *
     * @param aClazzList A类型的数据
     * @param bClazz     B类型的信息
     * @param <A>        源类型
     * @param <B>        目的类型
     * @return B类型的数据
     */
    public static <A, B> List<B> buildClassList(List<A> aClazzList, Class<B> bClazz) {
        List<B> bInstanceList = new ArrayList<B>();

        for (A aInstance : aClazzList) {
            if (bClazz.isInstance(aInstance)) {
                bInstanceList.add(bClazz.cast(aInstance));
            }
        }

        return bInstanceList;
    }

    /**
     * 定义一个函数接口
     *
     * @param <E> 类型名
     * @param <R> 方法名
     */
    @FunctionalInterface
    public interface SerializableFunction<E, R> extends Function<E, R>, Serializable {
    }
}
