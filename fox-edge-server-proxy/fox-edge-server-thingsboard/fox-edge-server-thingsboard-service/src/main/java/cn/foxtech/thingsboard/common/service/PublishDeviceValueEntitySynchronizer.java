package cn.foxtech.thingsboard.common.service;

import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.DifferUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Value只记录DeviceValue和TriggerValue的数据记录，它的变化非常高速，它在云端只是镜像副本数据
 * 工作过程：每次进行全量同步
 * 1、比对本地和云端的时间戳，判定是否需要进行同步，如果需要同步，就进行后面的流程
 * 2、向云端发出重置操作，云端接收到这个请求后，会清空自己的表数据
 * 3、向云端循环的分页提交本地mysql的全部数据，云端会将数据逐个的插入到自己的表总
 * 4、向云端发出完成操作，云端接收到这个操作后，会标识同步状态为完成
 * 5、至此，两边数据同步结束，重新等待本地的数据和时间戳发生变化，然后重新进行上述流程
 */
@Component
public class ValueEntitySynchronizer {
    private static final Logger logger = Logger.getLogger(ValueEntitySynchronizer.class);
    private final Map<String, Object> localAgileMap = new HashMap<>();
    private Object localTimeStamp = 0L;
    @Autowired
    private EntityManageService entityManageService;


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

            for (String key : addList) {
                Object redisTime = redisAgileMap.get(key);

                this.localAgileMap.put(key, redisTime);
            }

            for (String key : delList) {
                this.localAgileMap.remove(key);
            }

            for (String key : eqlList) {
                Object localTime = localAgileMap.get(key);
                Object redisTime = redisAgileMap.get(key);
                if (localTime.equals(redisTime)) {
                    continue;
                }
            }

            this.localTimeStamp = redisTimeStamp;


        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
