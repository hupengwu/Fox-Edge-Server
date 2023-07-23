package cn.foxtech.manager.system.scheduler;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.manager.system.service.Method2EntityService;
import cn.foxtech.common.entity.entity.DeviceTimeOutEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class Method2EntityScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PeriodTaskService.class);

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private Method2EntityService method2EntityService;

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

        // 不同实体之间的数据互相同步
        this.method2EntityService.syncEntity();

        // 清理超时记录的数据
        this.clearDeviceTimeOut();
    }

    /**
     * 删除设备的超时信息
     */
    private void clearDeviceTimeOut() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            // 获取配置参数
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, "deviceTimeOutConfig");
            if (configEntity == null) {
                return;
            }

            Map<String, Object> params = configEntity.getConfigValue();
            Integer lifetime = (Integer) params.get("lifetime");
            Integer period = (Integer) params.get("period");

            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            Long time = System.currentTimeMillis();

            // 删除：生命周期超过lifetime配置的对象
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceTimeOutEntity.class);
            for (BaseEntity entity : entityList) {
                DeviceTimeOutEntity deviceTimeOutEntity = (DeviceTimeOutEntity) entity;

                if (time - deviceTimeOutEntity.getCommFailedTime() > lifetime * 1000) {
                    this.entityManageService.deleteRDEntity(entity.makeServiceKey(), DeviceTimeOutEntity.class);
                    continue;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
