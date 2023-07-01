package cn.foxtech.common.utils.map;

import java.util.HashMap;
import java.util.Map;

/**
 * 两级哈希表，有很多数据存在层级管理，所有建立层级哈希表
 *
 * @param <K1>
 * @param <K2>
 * @param <V>
 */
public class HashK2VMap<K1, K2, V> extends AbstractK2VMap {
    /**
     * 获得1级数据
     *
     * @param key1
     * @return
     */
    public Map<K2, V> get1(K1 key1) {
        Map<K1, Map<K2, V>> k1k2Map = super.getMap();
        Map<K2, V> k2vMap = k1k2Map.get(key1);
        return k2vMap;
    }

    /**
     * 插入1级数据
     *
     * @param key1   key
     * @param k2vMap 1级数据
     * @return
     */
    public Map<K2, V> put1(K1 key1, Map<K2, V> k2vMap) {
        Map<K1, Map<K2, V>> k1k2Map = super.getMap();
        return k1k2Map.put(key1, k2vMap);
    }

    /**
     * 获得2级数据
     *
     * @param key1 key1
     * @param key2 key2
     * @return
     */
    public V get2(K1 key1, K2 key2) {
        Map<K1, Map<K2, V>> k1k2Map = super.getMap();
        Map<K2, V> k2vMap = k1k2Map.get(key1);
        if (k2vMap == null) {
            return null;
        }

        return k2vMap.get(key2);
    }

    /**
     * 插入2级数据
     *
     * @param key1
     * @param key2
     * @param value
     * @return
     */
    public V put2(K1 key1, K2 key2, V value) {
        Map<K1, Map<K2, V>> k1k2Map = super.getMap();

        Map<K2, V> k2vMap = k1k2Map.get(key1);
        if (k2vMap == null) {
            k2vMap = new HashMap<>();
            k1k2Map.put(key1, k2vMap);
        }

        return k2vMap.put(key2, value);
    }

    /**
     * 遍历树有数据
     * 使用范例：
     * hashK2VMap.foreach(new Reactor() {
     *
     * @param reactor 反应器
     * @Override public void react(Object key1, Object key2, Object value) {
     * <p>
     * });
     */
    public void foreach(Reactor reactor) {
        Map<K1, Map<K2, V>> k1k2Map = super.getMap();
        for (Map.Entry<K1, Map<K2, V>> entryK1 : k1k2Map.entrySet()) {
            K1 key1 = entryK1.getKey();
            Map<K2, V> k2vMap = entryK1.getValue();

            for (Map.Entry<K2, V> entryK2 : k2vMap.entrySet()) {
                reactor.react(key1, entryK2.getKey(), entryK2.getValue());
            }
        }

    }

    @FunctionalInterface
    public interface Reactor {
        void react(Object key1, Object key2, Object value);
    }
}
