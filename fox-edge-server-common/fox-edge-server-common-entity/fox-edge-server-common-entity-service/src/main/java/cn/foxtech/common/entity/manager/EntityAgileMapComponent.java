package cn.foxtech.common.entity.manager;


import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.entity.service.redis.BaseAgileMapRedisService;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 敏捷版的HashMap版的Redis的部件：它只将Agile时间戳装载到本地内存
 * 这样就可以达到减少内存占用，又可以快速感知redis的时间戳发生了变化
 */
@Data
@Component
public class EntityAgileMapComponent {
    /**
     * 消费者：Entity结构的数据
     */
    private final Set<String> consumer = new HashSet<>();

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    protected <T> AgileMapRedisService getBaseRedisService(Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            AgileMapRedisService consumerRedisService = AgileMapRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService;
        }

        return null;
    }

    protected <T> AgileMapRedisService getBaseRedisService(String simpleName) {
        if (this.consumer.contains(simpleName)) {
            AgileMapRedisService consumerRedisService = AgileMapRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            return consumerRedisService;
        }

        return null;
    }
}
