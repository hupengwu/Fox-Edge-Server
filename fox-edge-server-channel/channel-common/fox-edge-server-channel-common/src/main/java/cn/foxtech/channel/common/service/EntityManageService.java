/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.common.service;

import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ChannelStatusEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据实体业务
 */
@Component
public class EntityManageService extends EntityServiceManager {
    @Autowired
    private RedisService redisService;

    public void instance(Set<String> others) {
        this.instance(this.redisService);

        this.addConsumer(ConfigEntity.class.getSimpleName());
        this.addConsumer(ChannelEntity.class.getSimpleName());
        this.addConsumer(others);

        this.addProducer(ChannelStatusEntity.class.getSimpleName());

        this.getSourceRedis().add(ChannelStatusEntity.class.getSimpleName());
    }
}
