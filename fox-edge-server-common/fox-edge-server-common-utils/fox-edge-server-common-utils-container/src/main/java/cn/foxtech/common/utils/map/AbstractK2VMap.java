package cn.foxtech.common.utils.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 2级map的抽象类，作用是尽量把map的接口保留下来
 *
 * @param <K1>
 * @param <K2>
 * @param <V>
 */
public abstract class AbstractK2VMap<K1, K2, V> {
    private Map<K1, Map<K2, V>> k1k2Map = new HashMap<>();

    public Map<K1, Map<K2, V>> getMap() {
        return this.k1k2Map;
    }

    public void setMap(Map<K1, Map<K2, V>> k1k2Map) {
        this.k1k2Map = k1k2Map;
    }

    public Set<Map.Entry<K1, Map<K2, V>>> entrySet() {
        return this.k1k2Map.entrySet();
    }

    public Set<K1> keySet() {
        return this.k1k2Map.keySet();
    }

    public boolean isEmpty() {
        return this.k1k2Map.isEmpty();
    }

    public int size() {
        return this.k1k2Map.size();
    }

    public boolean containsKey(K1 key1) {
        return this.k1k2Map.containsKey(key1);
    }

    public Map<K2, V> remove(K1 key1) {
        return this.k1k2Map.remove(key1);
    }

    public Map<K2, V> get(K1 key1) {
        return this.k1k2Map.get(key1);
    }

    public Collection<Map<K2, V>> values() {
        return this.k1k2Map.values();
    }

    public Map<K2, V> put(K1 key1, Map<K2, V> k2vMap) {
        return this.k1k2Map.put(key1, k2vMap);
    }
}
