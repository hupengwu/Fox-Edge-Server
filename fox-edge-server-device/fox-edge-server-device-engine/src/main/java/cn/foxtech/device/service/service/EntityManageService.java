/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.service.service;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityPublishManager;
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
    private EntityPublishManager entityPublishManager;

    @Autowired
    private RedisService redisService;

    public void instance() {
        this.instance(this.redisService);

        Set<String> producer = this.entityRedisComponent.getProducer();
        Set<String> consumer = this.entityRedisComponent.getConsumer();

        // 告知：生产者如何装载数据源
        this.getSourceRedis().add(OperateMethodEntity.class.getSimpleName());

        producer.add(OperateMethodEntity.class.getSimpleName());

        consumer.add(DeviceEntity.class.getSimpleName());
        consumer.add(ChannelEntity.class.getSimpleName());
        consumer.add(OperateEntity.class.getSimpleName());
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(DeviceModelEntity.class.getSimpleName());

        // 数据的发布模式
        this.entityPublishManager.setPublishEntityUpdateTime(OperateMethodEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, OperateMethodEntity.class.getSimpleName());

    }
}
