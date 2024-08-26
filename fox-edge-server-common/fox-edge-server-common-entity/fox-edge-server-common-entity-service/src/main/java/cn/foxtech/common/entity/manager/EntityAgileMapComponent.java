/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;


import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 敏捷版的HashMap版的Redis的部件：它只将Agile时间戳装载到本地内存
 * 这样就可以达到减少内存占用，又可以快速感知redis的时间戳发生了变化
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class EntityAgileMapComponent {
    /**
     * 消费者：Entity结构的数据
     */
    private final Set<String> consumer = new HashSet<>();

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
