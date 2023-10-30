package cn.foxtech.thingspanel.service.service;

import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.cloud.common.service.EntityManageService;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeviceValuePeriodSynchronizer {
    private static final Logger logger = Logger.getLogger(DeviceValuePeriodSynchronizer.class);

    private Long lastTimeStamp = 0L;
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DeviceValueSynchronizer synchronizer;

    @Setter
    private Long interval = 30 * 60 * 1000L;

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

            // 当前时间
            Long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < this.lastTimeStamp + 30 * 60 * 1000L) {
                return;
            }
            this.lastTimeStamp = currentTimeMillis;

            // 读取redis的数据
            Map<String, Object> redisHashMap = redisReader.readHashMap();

            // 推送全量数据
            this.synchronizer.publish(redisHashMap.keySet(), redisHashMap);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
