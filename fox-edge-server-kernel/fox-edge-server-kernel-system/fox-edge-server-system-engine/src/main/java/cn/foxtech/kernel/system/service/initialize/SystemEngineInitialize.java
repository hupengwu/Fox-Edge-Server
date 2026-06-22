/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.devicesequence.DeviceSequenceEntityService;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.common.initialize.CommonInitialize;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.repository.initialize.RepoInitialize;
import cn.foxtech.kernel.system.service.restfullike.mqtt.MqttRestfulLikeService;
import cn.foxtech.kernel.system.service.restfullike.redis.RedisRestfulLikeController;
import cn.foxtech.kernel.system.service.restfullike.redis.RedisRestfulLikeScheduler;
import cn.foxtech.kernel.system.service.task.CleanCacheTask;
import cn.foxtech.kernel.system.service.task.CleanLogFileTask;
import cn.foxtech.kernel.system.service.task.ConfigEntityTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化
 */
@Component
public class SystemEngineInitialize {
    private static final Logger logger = LoggerFactory.getLogger(SystemEngineInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;


    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;


    @Autowired
    private CommonInitialize commonInitialize;

    @Autowired
    private RepoInitialize repoInitialize;

    @Autowired
    private EdgeService edgeService;

    /**
     * 定时清理操作系统的Cache
     */
    @Autowired
    private CleanCacheTask cleanCacheTask;

    @Autowired
    private ConfigEntityTask configEntityTask;

    /**
     * 删除失效链路任务
     */
    @Autowired
    private CleanLogFileTask cleanLogFileTask;

    @Autowired
    private MqttRestfulLikeService mqttRestfulLikeService;

    @Autowired
    private RedisRestfulLikeController redisRestfulLikeController;

    @Autowired
    private DeviceSequenceEntityService deviceSequenceEntityService;


    @Autowired
    private RedisRestfulLikeScheduler redisRestfulLikeScheduler;

    public void initialize() {
        String message = "------------------------SystemEngine 初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.commonInitialize.initialize();

        this.repoInitialize.initialize();

        // RestfulLike的mqtt和redis的消息响应
        this.mqttRestfulLikeService.initialize();
        this.redisRestfulLikeController.initialize();
        this.redisRestfulLikeScheduler.schedule();

        this.createPeriodTask();

        message = "------------------------SystemEngine 初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

    private void createPeriodTask() {
        // 检查：只有在非docker模式下，才能进行下列操作
        if (!this.edgeService.isDockerEnv()) {
            this.periodTasksScheduler.insertPeriodTask(this.cleanCacheTask);
        }

        this.periodTasksScheduler.insertPeriodTask(this.cleanLogFileTask);
        this.periodTasksScheduler.insertPeriodTask(this.configEntityTask);
    }

}
