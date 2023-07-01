package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RedisWriter {
    /**
     * redis
     */
    private RedisTemplate redisTemplate;

    /**
     * 边缘服务器：数据库表的名称
     */
    private String entityType;

    private String getHead() {
        return "fox.edge.entity." + this.getEntityType() + ".";
    }


    public synchronized void writeEntityMap(Map<String, BaseEntity> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }

        Long time = System.currentTimeMillis();

        // 构造敏捷数据
        Map<String, Long> agileMap = new HashMap<>();
        for (String key : dataMap.keySet()) {
            agileMap.put(key, time);
        }

        this.redisTemplate.opsForHash().putAll(this.getHead() + "agile", agileMap);
        this.redisTemplate.opsForHash().putAll(this.getHead() + "data", dataMap);
        this.redisTemplate.opsForValue().set(this.getHead() + "sync", time);
    }

    public synchronized void deleteEntity(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        Long time = System.currentTimeMillis();


        for (String key : keys) {
            this.redisTemplate.opsForHash().delete(this.getHead() + "agile", key);
            this.redisTemplate.opsForHash().delete(this.getHead() + "data", key);
            this.redisTemplate.opsForValue().set(this.getHead() + "sync", time);
        }
    }

    public void deleteEntity(String key) {
        Set<String> keys = new HashSet<>();
        keys.add(key);

        this.deleteEntity(keys);
    }

    public void writeEntityList(List<BaseEntity> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        Map<String, BaseEntity> dataMap = new HashMap<>();
        for (BaseEntity entity : dataList) {
            dataMap.put(entity.makeServiceKey(), entity);
        }

        this.writeEntityMap(dataMap);
    }

    public void writeEntity(BaseEntity entity) {
        if (entity == null) {
            return;
        }

        Map<String, BaseEntity> dataMap = new HashMap<>();
        dataMap.put(entity.makeServiceKey(), entity);

        this.writeEntityMap(dataMap);
    }
}
