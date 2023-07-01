package cn.foxtech.common.entity.service.redis;


import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisReaderService {
    private static final Map<String, RedisReader> map = new ConcurrentHashMap<>();


    public static synchronized <T> RedisReader newInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        RedisReader instance = new RedisReader();
        instance.setEntityType(clazz.getSimpleName());
        instance.setRedisTemplate(redisTemplate);
        return instance;
    }

    public static synchronized <T> RedisReader getInstanceBySimpleName(String clazzSimpleName, RedisTemplate redisTemplate) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        RedisReader instance = new RedisReader();
        instance.setEntityType(clazzSimpleName);
        instance.setRedisTemplate(redisTemplate);

        map.put(instance.getEntityType(), instance);
        return map.get(clazzSimpleName);
    }

    public static <T> RedisReader getInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisTemplate);
    }
}
