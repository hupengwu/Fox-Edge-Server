/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.scheduler;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.tags.RedisTagService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.persist.common.history.IDeviceHistoryUpdater;
import cn.foxtech.persist.common.service.DeviceObjectMapper;
import cn.foxtech.persist.common.service.PersistEnvService;
import cn.foxtech.persist.common.service.PersistManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class PersistManageScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PersistManageScheduler.class);

    @Autowired
    private PersistManageService entityManageService;

    @Autowired
    private InitialConfigService configService;

    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    @Autowired
    private IDeviceHistoryUpdater hisdoryEntityUpdater;

    @Autowired
    private RedisTagService redisTagService;

    @Autowired
    private PersistEnvService persistEnvService;

    /**
     * 上次处理时间
     */
    private long lastTime = 0;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        this.entityManageService.syncEntity();

        // 同步映射数据
        this.deviceObjectMapper.syncEntity();

        // 保存标记
        this.redisTagService.save();

        // 删除设备历史记录
        this.hisdoryEntityUpdater.clearHistoryEntity();

        // 删除操作记录
        this.clearOperateRecord();
    }


    private void clearOperateRecord() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            String serverConfig = this.persistEnvService.getServerConfig();
            Map<String, Object> configs = this.configService.getConfigParam(serverConfig);
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("operateRecord", new HashMap<>());

            Integer maxCount = (Integer) params.getOrDefault("maxCount", 10000);
            Integer period = (Integer) params.getOrDefault("period", 3600);


            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            // 除了最近的maxCount条数据，旧数据全部删除
            this.entityManageService.getOperateRecordEntityService().delete(maxCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
