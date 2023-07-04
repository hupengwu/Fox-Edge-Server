package cn.foxtech.trigger.service.scheduler;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.trigger.service.service.EntityManageService;
import cn.foxtech.trigger.service.trigger.TriggerValueUpdater;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceValueNotifyScheduler extends PeriodTaskService {
    private final Map<String, Object> agileTime = new ConcurrentHashMap<>();
    private Object syncTime = -1l;
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private TriggerValueUpdater triggerValueUpdater;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class);

        // 检查：总标记是否发生变化
        Object syncRedis = redisReader.readSync();
        if (syncRedis == null) {
            return;
        }
        if (this.syncTime.equals(syncRedis)) {
            return;
        }

        Map<String, Object> agileRedis = redisReader.readAgileMap();
        if (agileRedis == null) {
            return;
        }

        // 根据时间戳，判定变化的数据
        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(this.agileTime.keySet(), agileRedis.keySet(), addList, delList, eqlList);

        // 删除的数据
        for (String key : delList) {
            this.agileTime.remove(key);
        }

        Set<Object> updateSet = new HashSet<>();

        // 新增的数据
        for (String key : addList) {
            updateSet.add(key);
            this.agileTime.put(key, agileRedis.get(key));
        }


        // 修改的数据
        for (String key : eqlList) {
            Object oldTime = this.agileTime.get(key);
            Object newTime = agileRedis.get(key);
            if (oldTime == null || newTime == null) {
                continue;
            }

            if (oldTime.equals(newTime)) {
                continue;
            }

            updateSet.add(key);
            this.agileTime.put(key, agileRedis.get(key));
        }

        // 读取变化的数据实体
        Map<String, BaseEntity> entityMap = redisReader.readEntityMap(updateSet);

        // 执行触发器更新
        for (String key : entityMap.keySet()) {
            this.triggerValueUpdater.update(entityMap.get(key));
        }


        // 更新总时间
        this.syncTime = syncRedis;
    }
}
