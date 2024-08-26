/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.controller.common.initialize;

import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.controller.common.scheduler.ControllerManageScheduler;
import cn.foxtech.controller.common.service.ControllerEnvService;
import cn.foxtech.controller.common.service.ControllerManageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Data
@Component
public class ControllerInitialize {
    /**
     * 实体管理
     */
    @Autowired
    private ControllerManageService entityManageService;

    /**
     * 实体调度
     */
    @Autowired
    private ControllerManageScheduler entityManageScheduler;

    /**
     * 进程状态
     */
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private ControllerEnvService controllerEnvService;


    public void initialize() {
        // 进程状态
        if (!this.controllerEnvService.isCompose()) {
            this.serviceStatusScheduler.initialize();
            this.serviceStatusScheduler.schedule();
        }

        // 装载数据实体，并启动同步线程
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();
        this.entityManageScheduler.schedule();
    }
}
