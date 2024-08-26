/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.scheduler;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.rpc.redis.persist.server.RedisListPersistServer;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.persist.common.service.EntityUpdateService;
import cn.foxtech.persist.common.service.PersistManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class RedisListValueScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(RedisListValueScheduler.class);
    @Autowired
    PersistManageService entityManageService;
    @Autowired
    EntityUpdateService entityUpdateService;

    /**
     * 设备数值
     */
    @Autowired
    private RedisListPersistServer persistServer;

    /**
     * 前台日志
     */
    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        Object respondMap = this.persistServer.popValueRequest(1, TimeUnit.SECONDS);
        if (respondMap == null) {
            return;
        }

        // 处理数据
        this.updateDeviceRespond(respondMap);
    }


    private void updateDeviceRespond(Object respond) {
        try {
            TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO((Map<String, Object>) respond);
            for (OperateRespondVO operateRespondVO : taskRespondVO.getRespondVOS()) {
                this.entityUpdateService.updateDeviceRespond(operateRespondVO, taskRespondVO.getClientName());
            }
        } catch (Exception e) {
            String message = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }
}