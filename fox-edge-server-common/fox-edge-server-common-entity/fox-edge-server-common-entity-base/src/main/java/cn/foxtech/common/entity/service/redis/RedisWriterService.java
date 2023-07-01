package cn.foxtech.common.entity.service.redis;


import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisWriterService {
    private static final Map<String, RedisWriter> map = new ConcurrentHashMap<>();


    public static synchronized <T> RedisWriter newInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        RedisWriter instance = new RedisWriter();
        instance.setEntityType(clazz.getSimpleName());
        instance.setRedisTemplate(redisTemplate);
        return instance;
    }

    public static synchronized <T> RedisWriter getInstanceBySimpleName(String clazzSimpleName, RedisTemplate redisTemplate) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        RedisWriter instance = new RedisWriter();
        instance.setEntityType(clazzSimpleName);
        instance.setRedisTemplate(redisTemplate);

        map.put(instance.getEntityType(), instance);
        return map.get(clazzSimpleName);
    }

    public static <T> RedisWriter getInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisTemplate);
    }
}
