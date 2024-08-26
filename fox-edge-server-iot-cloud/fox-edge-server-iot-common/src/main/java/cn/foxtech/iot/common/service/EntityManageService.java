/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.iot.common.service;

import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Redis实体管理器：通过它，可以对Redis实体进行读写操作
 */
@Component
public class EntityManageService extends EntityServiceManager {
    @Autowired
    private RedisService redisService;

    public void instance() {
        this.instance(this.redisService);
    }
}
