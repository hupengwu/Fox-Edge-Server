package cn.foxtech.common.entity.service.redis;

import cn.foxtech.utils.common.utils.redis.service.RedisService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用消费者
 */
public class ConsumerRedisService extends BaseConsumerRedisService {
    private static final Map<String, ConsumerRedisService> map = new ConcurrentHashMap<>();

    private String entityType;

    private RedisService redisService;

    public static synchronized <T> ConsumerRedisService newInstance(Class<T> clazz, RedisService redisService) {
        ConsumerRedisService instance = new ConsumerRedisService();
        instance.entityType = clazz.getSimpleName();
        instance.redisService = redisService;
        return instance;
    }

    public static synchronized <T> ConsumerRedisService getInstanceBySimpleName(String clazzSimpleName, RedisService redisService) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        ConsumerRedisService instance = new ConsumerRedisService();
        instance.entityType = clazzSimpleName;
        instance.redisService = redisService;

        map.put(instance.entityType, instance);
        return map.get(clazzSimpleName);
    }

    public static <T> ConsumerRedisService getInstance(Class<T> clazz, RedisService redisService) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisService);
    }


    public RedisService getRedisService() {
        return this.redisService;
    }

    public String getEntityType() {
        return this.entityType;
    }
}
