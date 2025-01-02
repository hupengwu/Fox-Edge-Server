/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.common.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.common.initialize.KernelInitialize;
import cn.foxtech.kernel.system.common.scheduler.EntityManageScheduler;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.common.task.GateWayRouteUpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class CommonInitialize {
    private static final Logger logger = LoggerFactory.getLogger(CommonInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private KernelInitialize kernelInitialize;


    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Autowired
    private GateWayRouteUpdateTask gateWayRouteUpdateTask;

    @Value("${spring.fox-service.mode.router}")
    private String routerMode;

    public void initialize() {
        String message = "------------------------CommonInitialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.kernelInitialize.initialize();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        // 添加周期任务
        this.createPeriodTask();

        message = "------------------------CommonInitialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

    private void createPeriodTask() {
        // 启动周期任务线程
        this.periodTasksScheduler.schedule();

        // 检查：本地工作模式下，向gateway服务手动注册路由
        if ("local".equals(this.routerMode)) {
            this.periodTasksScheduler.insertPeriodTask(this.gateWayRouteUpdateTask);
        }
    }
}
