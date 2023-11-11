package cn.foxtech.manager.system.task;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceTimeOutEntity;
import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CleanTimeOutTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ConfigManageService configManageService;

    /**
     * 上次处理时间
     */
    private long lastTime = 0;


    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 60;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            // 获取配置参数
            Map<String, Object> configValue = this.configManageService.getConfigParam("deviceTimeOutConfig");

            Integer lifetime = (Integer) configValue.getOrDefault("lifetime", 3600);
            Integer period = (Integer) configValue.getOrDefault("period", 3600);

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
