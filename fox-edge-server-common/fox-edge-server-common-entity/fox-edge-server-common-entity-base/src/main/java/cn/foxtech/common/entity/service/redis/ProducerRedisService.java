/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.redis;

import cn.foxtech.utils.common.utils.redis.service.RedisService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存镜像：生产者
 * 缓存镜像：会在本地内存中保存一份和redis中一致的副本。这样应用可以将数据高速写入本地缓存中，后台异步线程
 * 会定期将缓存中的变更数据，写入到redis当中。这样就解决了直接读redis带来的延迟问题。
 * 缓存镜像适合高速处理的本地数据，但是要付出镜像内存的开销。
 */
public class ProducerRedisService extends BaseProducerRedisService {
    private static final Map<String, ProducerRedisService> map = new ConcurrentHashMap<>();

    private String entityType;

    private RedisService redisService;

    public static synchronized ProducerRedisService getInstanceBySimpleName(String clazzSimpleName, RedisService redisService) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        ProducerRedisService instance = new ProducerRedisService();
        instance.entityType = clazzSimpleName;
        instance.redisService = redisService;

        map.put(instance.entityType, instance);
        return map.get(clazzSimpleName);
    }

    public static synchronized void removeInstanceBySimpleName(ProducerRedisService producerRedisService) {
        map.remove(producerRedisService.entityType);
    }

    public static <T> ProducerRedisService getInstance(Class<T> clazz, RedisService redisService) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisService);
    }

    public static <T> ProducerRedisService getInstance(String simpleName, RedisService redisService) {
        return getInstanceBySimpleName(simpleName, redisService);
    }

    public RedisService getRedisService() {
        return this.redisService;
    }

    public String getEntityType() {
        return this.entityType;
    }
}
