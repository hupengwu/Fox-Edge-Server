package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.iot.fox.cloud.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
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


    /**
     * 实体管理者
     */
    @Autowired
    private ValueEntityLocalDataBase entityLocalDataBase;

    @Autowired
    private EntityManageService publishEntityManageService;

    /**
     * 云端发布者
     */
    @Autowired
    private ValueEntityRemoteCloud entityRemoteCloud;


    public void syncEntity(String edgeId) {
        try {
            Set<String> entityTypeList = this.entityLocalDataBase.getEntityTypeList();

            for (String entityType : entityTypeList) {
                this.syncEntity(edgeId, entityType);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void syncEntity(String edgeId, String entityType) {
        try {
            // 获得直接读取redis的部件
            RedisReader redisReader = this.publishEntityManageService.getRedisReader(entityType);
            if (redisReader == null) {
                return;
            }

            // 获取redis上的时间戳,用来判断是否本地redis发生了变化
            Object localTimeStamp = redisReader.readSync();
            if (localTimeStamp == null) {
                return;
            }

            // 上次的本地时间戳
            Object lastTimeStamp = this.entityLocalDataBase.getSyncObject(entityType);
            if (Long.parseLong(localTimeStamp.toString()) <= 0) {
                return;
            }

            // 时间差
            Long span = Long.parseLong(localTimeStamp.toString()) - Long.parseLong(lastTimeStamp.toString());
            if (span < 3600 * 1000) {
                return;
            }

            List<Map<String, Object>> entityList = redisReader.readHashMapList();

            // 阶段1：发出重置请求：此时云端会清空自己的表数据
            Map<String, Object> respond = this.entityRemoteCloud.publishReset(edgeId, entityType, localTimeStamp.toString());
            if (!HttpStatus.SUCCESS.equals(respond.get("code"))) {
                return;
            }

            // 阶段2：发布数据到云端
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("insert", entityList);
            this.entityRemoteCloud.publishEntity(edgeId, entityType, localTimeStamp.toString(), dataMap);

            // 阶段3：发出完成标记：此时云端会标记已经完成
            this.entityRemoteCloud.publishComplete(edgeId, entityType, localTimeStamp.toString());

            this.entityLocalDataBase.setSyncObject(entityType, Long.parseLong(localTimeStamp.toString()));
        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
        }
    }
}
