package cn.foxtech.common.tags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 写入全局标志
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class RedisTagWriter {
    /**
     * redis
     */
    @Autowired
    private RedisTemplate redisTemplate;


    private String getHead() {
        return "fox.edge.tags.";
    }


    public synchronized void writeTags(Map<String, Object> tagMap) {
        if (tagMap == null || tagMap.isEmpty()) {
            return;
        }

        Long time = System.currentTimeMillis();

        // 构造敏捷数据
        Map<String, Long> agileMap = new HashMap<>();
        for (String key : tagMap.keySet()) {
            agileMap.put(key, time);
        }

        this.redisTemplate.opsForHash().putAll(this.getHead() + "agile", agileMap);
        this.redisTemplate.opsForHash().putAll(this.getHead() + "data", tagMap);
        this.redisTemplate.opsForValue().set(this.getHead() + "sync", time);
    }

    public synchronized void deleteTags(Set<String> entityTypes) {
        if (entityTypes == null || entityTypes.isEmpty()) {
            return;
        }

        Long time = System.currentTimeMillis();


        for (String key : entityTypes) {
            this.redisTemplate.opsForHash().delete(this.getHead() + "agile", key);
            this.redisTemplate.opsForHash().delete(this.getHead() + "data", key);
            this.redisTemplate.opsForValue().set(this.getHead() + "sync", time);
        }
    }

    public void deleteTag(String entityType) {
        Set<String> keys = new HashSet<>();
        keys.add(entityType);

        this.deleteTags(keys);
    }


    public void writeTag(String entityType, Object entity) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(entityType, entity);

        this.writeTags(dataMap);
    }
}
