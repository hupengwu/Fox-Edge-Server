/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.controller.common.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 实体管理操作：提供设备信息查询和构造服务
 */
@Component
public class ControllerManageService extends EntityServiceManager {
    @Autowired
    private RedisService redisService;

    public void instance() {
        this.instance(this.redisService);

        Set<String> consumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();

        consumer.add(OperateEntity.class.getSimpleName());
        consumer.add(DeviceEntity.class.getSimpleName());
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(OperateMonitorTaskEntity.class.getSimpleName());

        reader.add(DeviceValueEntity.class.getSimpleName());
    }
}
