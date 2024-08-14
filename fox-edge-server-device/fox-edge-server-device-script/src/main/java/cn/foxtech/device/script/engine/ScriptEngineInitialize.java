/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScriptEngineInitialize {
    @Autowired
    private EntityServiceManager entityServiceManager;

    @Autowired
    private OperateNotify operateNotify;

    public void initialize() {
        // 为操纵绑定JSP引擎
        List<BaseEntity> operateEntityList = this.entityServiceManager.getEntityList(OperateEntity.class);
        for (BaseEntity entity : operateEntityList) {
            this.operateNotify.rebindScriptEngine(entity);
        }

        // 绑定一个类型级别的数据变更通知
        ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityServiceManager.getBaseRedisService(OperateEntity.class);
        consumerRedisService.bind(this.operateNotify);
    }
}
