/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.service.common.initialize;

import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.service.common.scheduler.ServiceEntityManageScheduler;
import cn.foxtech.service.common.service.ServiceEntityManageService;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Data
@Component
public class ServiceCommonInitialize {
    private static final Logger logger = Logger.getLogger(ServiceCommonInitialize.class);

    /**
     * 实体管理
     */
    @Autowired
    private ServiceEntityManageService entityManageService;

    /**
     * 实体调度
     */
    @Autowired
    private ServiceEntityManageScheduler entityManageScheduler;

    /**
     * 进程状态
     */
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;


    public void initialize() {
        // 进程状态
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 初始化数据管理器
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();
        this.entityManageScheduler.schedule();
    }
}
