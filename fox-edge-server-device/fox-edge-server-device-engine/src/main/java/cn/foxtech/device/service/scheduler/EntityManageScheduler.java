/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.service.scheduler;


import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.device.service.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class EntityManageScheduler extends PeriodTaskService {
    @Autowired
    private EntityManageService entityManageService;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        this.entityManageService.syncEntity();
    }
}
