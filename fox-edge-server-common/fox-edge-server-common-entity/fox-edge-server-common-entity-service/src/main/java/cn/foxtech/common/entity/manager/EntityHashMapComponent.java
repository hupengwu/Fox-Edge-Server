package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.service.redis.HashMapRedisService;
import cn.foxtech.common.entity.service.redis.BaseHashMapRedisService;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * HashMap版的Redis的部件：它将数据以HashMap的方式，保存到本地缓存。
 * 这样的好处是，消费这个数据的服务，并不需要知道跟生产者结构化定义
 */
@Data
@Component
public class EntityHashMapComponent {
    /**
     * 消费者：Entity结构的数据
     */
    private final Set<String> consumer = new HashSet<>();

    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    private RedisService redisService;

    protected <T> T getEntity(Long id, Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) consumerRedisService.getEntity(id);
        }

        return null;
    }

    protected <T> T getEntity(String entityKey, Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) consumerRedisService.getEntity(entityKey);
        }

        return null;
    }


    protected <T> List<Map<String, Object>> getEntityList(Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityList();
        }

        return new ArrayList<>();
    }

    protected <T> BaseHashMapRedisService getBaseRedisService(Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService;
        }

        return null;
    }

    protected <T> HashMapRedisService getBaseRedisService(String simpleName) {
        if (this.consumer.contains(simpleName)) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            return consumerRedisService;
        }

        return null;
    }
}
