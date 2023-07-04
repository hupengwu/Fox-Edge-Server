package cn.foxtech.persist.common.scheduler;


import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.persist.common.service.DeviceObjectMapper;
import cn.foxtech.persist.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class EntityManageScheduler extends PeriodTaskService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

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

        // 删除设备历史记录
        this.clearDeviceHistoryEntity();

        // 删除操作记录
        this.clearOperateRecord();
    }

    private void clearDeviceHistoryEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            // 获取配置参数
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "deviceHistoryConfig");
            if (configEntity == null) {
                return;
            }

            Map<String, Object> params = configEntity.getConfigValue();
            Integer maxCount = (Integer) params.get("maxCount");
            Integer period = (Integer) params.get("period");

            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            // 除了最近的maxCount条数据，旧数据全部删除
            this.entityManageService.getDeviceHistoryEntityService().delete(maxCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void clearOperateRecord() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            // 获取配置参数
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "operateRecordConfig");
            if (configEntity == null) {
                return;
            }

            Map<String, Object> params = configEntity.getConfigValue();
            Integer maxCount = (Integer) params.get("maxCount");
            Integer period = (Integer) params.get("period");

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
