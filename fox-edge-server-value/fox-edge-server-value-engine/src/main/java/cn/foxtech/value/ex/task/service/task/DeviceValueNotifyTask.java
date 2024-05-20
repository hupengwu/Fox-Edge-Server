package cn.foxtech.value.ex.task.service.task;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.DeviceValueExObjectValue;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.value.ex.task.service.service.DataCacheManager;
import cn.foxtech.value.ex.task.service.service.EntityManageService;
import cn.foxtech.value.ex.task.service.service.TaskEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 周期性GC任务
 */
@Component
public class DeviceValueNotifyTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DataCacheManager dataTaskManager;

    @Autowired
    private TaskEngineService taskEngineService;


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
        return 1;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            AgileMapRedisService redisService = this.entityManageService.getAgileMapService(DeviceValueEntity.class.getSimpleName());

            // 装载数据：从redis读取数据，并获知变化状态
            Map<String, BaseEntity> addMap = new HashMap<>();
            Set<String> delSet = new HashSet<>();
            Map<String, BaseEntity> mdyMap = new HashMap<>();
            redisService.loadChangeEntities(addMap, delSet, mdyMap, new DeviceValueEntity());

            // 检测：数据
            if (addMap.isEmpty() && delSet.isEmpty() && mdyMap.isEmpty()) {
                return;
            }

            for (String key : addMap.keySet()) {
                DeviceValueEntity valueEntity = (DeviceValueEntity) addMap.get(key);
                Map<String, DeviceValueExObjectValue> deviceMap = this.dataTaskManager.append(valueEntity);
                this.taskEngineService.evalScript(valueEntity, deviceMap);
            }
            for (String key : mdyMap.keySet()) {
                DeviceValueEntity valueEntity = (DeviceValueEntity) mdyMap.get(key);
                Map<String, DeviceValueExObjectValue> deviceMap = this.dataTaskManager.append(valueEntity);
                this.taskEngineService.evalScript(valueEntity, deviceMap);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
