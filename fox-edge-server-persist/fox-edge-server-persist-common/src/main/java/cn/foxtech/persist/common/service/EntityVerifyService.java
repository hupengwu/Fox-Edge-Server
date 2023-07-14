package cn.foxtech.persist.common.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceObjectEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.common.utils.DifferUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EntityVerifyService {
    private static final Logger logger = Logger.getLogger(EntityVerifyService.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 验证措施：验证mysql的object与redis的value之间的一致性，并进行预处理
     * 预处理方式：
     * 1、在redis上，删除跟mysql没有的DeviceValueEntity
     * 2、在redis上，删除跟mysql不一致的DeviceValueEntity
     * 说明：
     * 1、在mysql上比redis多的数据，这是一个正常场景，因为redis毕竟只是临时性的数据而已，
     * 比如用户清空redis，重启等各自原因
     * 2、对于真正意义上的mysql垃圾数据，用户会在用户界面上，主动通知持久化服务删除它们
     */
    public void initialize() {
        try {
            List<BaseEntity> objectEntityList = this.entityManageService.getDeviceObjectEntityService().selectEntityList();
            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class);
            RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceValueEntity.class);

            List<BaseEntity> valueEntityList = redisReader.readEntityList();


            // 重组mysql的数据
            Map<String, Set<String>> mysqlMap = new HashMap<>();
            for (BaseEntity entity : objectEntityList) {
                DeviceObjectEntity objectEntity = (DeviceObjectEntity) entity;

                DeviceValueEntity valueEntity = new DeviceValueEntity();
                valueEntity.setDeviceName(objectEntity.getDeviceName());
                Set<String> objectNames = mysqlMap.computeIfAbsent(valueEntity.makeServiceKey(), k -> new HashSet<>());
                objectNames.add(objectEntity.getObjectName());
            }

            // 重组redis的数据
            Map<String, Set<String>> redisMap = new HashMap<>();
            for (BaseEntity entity : valueEntityList) {
                DeviceValueEntity valueEntity = (DeviceValueEntity) entity;

                Set<String> objectNames = redisMap.computeIfAbsent(valueEntity.makeServiceKey(), k -> new HashSet<>());
                objectNames.addAll(valueEntity.getParams().keySet());
            }

            Set<String> addList = new HashSet();
            Set<String> delList = new HashSet();
            Set<String> eqlList = new HashSet();
            DifferUtils.differByValue(redisMap.keySet(), mysqlMap.keySet(), addList, delList, eqlList);

            // 在redis上，删除跟mysql没有的DeviceValueEntity
            redisWriter.deleteEntity(delList);

            // 删除对象不一致的redis数据
            for (String serviceKey : eqlList) {
                Set<String> redis = redisMap.get(serviceKey);
                Set<String> mysql = mysqlMap.get(serviceKey);

                // 比较对象级别是否存在差异
                if (!DifferUtils.differByValue(redis, mysql)) {
                    continue;
                }

                // 在redis上，删除跟mysql不一致的DeviceValueEntity
                redisWriter.deleteEntity(serviceKey);
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }
}
