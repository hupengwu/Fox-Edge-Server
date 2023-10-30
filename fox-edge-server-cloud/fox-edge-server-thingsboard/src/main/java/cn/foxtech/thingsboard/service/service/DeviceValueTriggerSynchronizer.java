package cn.foxtech.thingsboard.service.service;

import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.cloud.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class DeviceValueTriggerSynchronizer {
    private static final Logger logger = Logger.getLogger(DeviceValuePeriodSynchronizer.class);
    private final Map<String, Object> localAgileMap = new HashMap<>();

    private Object localTimeStamp = 0L;

    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private DeviceValueSynchronizer synchronizer;

    public void syncEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }
            // 获得直接读取redis的部件
            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class.getSimpleName());
            if (redisReader == null) {
                return;
            }

            // 获取redis上的时间戳,用来判断是否本地redis发生了变化
            Object redisTimeStamp = redisReader.readSync();
            if (redisTimeStamp == null) {
                return;
            }

            // 比较时间戳
            if (redisTimeStamp.equals(this.localTimeStamp)) {
                return;
            }

            // redis的数据
            Map<String, Object> redisAgileMap = redisReader.readAgileMap();
            if (redisAgileMap == null) {
                return;
            }

            Set<String> addList = new HashSet<>();
            Set<String> delList = new HashSet<>();
            Set<String> eqlList = new HashSet<>();
            DifferUtils.differByValue(this.localAgileMap.keySet(), redisAgileMap.keySet(), addList, delList, eqlList);

            Set<String> pushKeys = new HashSet<>();

            // 新增的的数据，需要推送
            for (String key : addList) {
                Object redisTime = redisAgileMap.get(key);
                this.localAgileMap.put(key, redisTime);

                pushKeys.add(key);
            }

            // 删除的数据，不需要推送
            for (String key : delList) {
                this.localAgileMap.remove(key);
            }

            // 变更的数据，需要推送
            for (String key : eqlList) {
                Object localTime = localAgileMap.get(key);
                Object redisTime = redisAgileMap.get(key);
                if (localTime.equals(redisTime)) {
                    continue;
                }

                pushKeys.add(key);
            }

            this.localTimeStamp = redisTimeStamp;

            if (!pushKeys.isEmpty()) {
                Map<String, Object> redisHashMap = redisReader.readHashMap();
                this.synchronizer.publish(pushKeys, redisHashMap);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
