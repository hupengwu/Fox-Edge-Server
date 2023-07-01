package cn.foxtech.common.entity.service.redis;

import cn.foxtech.utils.common.utils.redis.service.RedisService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 敏捷数据消费者
 */
public class AgileMapRedisService extends BaseAgileMapRedisService {
    private static final Map<String, AgileMapRedisService> map = new ConcurrentHashMap<>();
    private String entityType;
    private RedisService redisService;

    public static synchronized <T> AgileMapRedisService newInstance(Class<T> clazz, RedisService redisService) {
        AgileMapRedisService instance = new AgileMapRedisService();
        instance.entityType = clazz.getSimpleName();
        instance.redisService = redisService;
        return instance;
    }

    public static synchronized <T> AgileMapRedisService getInstanceBySimpleName(String clazzSimpleName, RedisService redisService) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        AgileMapRedisService instance = new AgileMapRedisService();
        instance.entityType = clazzSimpleName;
        instance.redisService = redisService;

        map.put(instance.entityType, instance);
        return map.get(clazzSimpleName);
    }

    public static <T> AgileMapRedisService getInstance(Class<T> clazz, RedisService redisService) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisService);
    }

    @Override
    public RedisService getRedisService() {
        return this.redisService;
    }

    @Override
    public String getEntityType() {
        return this.entityType;
    }
}
