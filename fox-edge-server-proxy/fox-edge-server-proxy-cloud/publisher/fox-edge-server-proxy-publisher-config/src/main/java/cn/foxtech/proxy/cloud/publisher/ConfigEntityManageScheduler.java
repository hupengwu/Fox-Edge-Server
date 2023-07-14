package cn.foxtech.proxy.cloud.publisher;


import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.proxy.cloud.publisher.service.CloudEntityManageService;
import cn.foxtech.proxy.cloud.publisher.service.CloudEntityRemoteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class ConfigEntityManageScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(ConfigEntityManageScheduler.class);

    private final Set<String> hasResetMap = new HashSet<>();

    @Autowired
    private RedisConsoleService consoleService;

    /**
     * 实体管理者
     */
    @Autowired
    private CloudEntityManageService publishEntityManageService;

    /**
     * 云端发布者
     */
    @Autowired
    private ConfigEntityPublishService configEntityPublishService;

    @Autowired
    private CloudEntityRemoteService remoteService;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 检查：是否已经初始化
        if (!this.publishEntityManageService.isInitialized()) {
            return;
        }

        // 云端是否处于锁定状态
        if (!this.remoteService.isLogin() && this.remoteService.isLockdown()) {
            return;
        }

        Set<String> entityTypeList = this.publishEntityManageService.getPublishEntityTypeList(EntityPublishConstant.value_mode_config);

        // 检查：边缘节点Cache和云端节点Redis的同步状态
        this.operatePublishCheck(entityTypeList);

        // 如果需要复位，则复位边缘节点Cache和云端节点Redis的同步状态
        this.operatePublishReset(entityTypeList);

        // 增量更新云端节点的Redis数据
        this.operatePublishUpdate(entityTypeList);

        // 休眠一段时间，避免太频繁的发包
        Thread.sleep(0);
    }

    /**
     * 对这些实体类型，进行向云端发布Reset动作，也就是复位双方的状态
     *
     * @param entityTypeList
     */
    private void operatePublishReset(Set<String> entityTypeList) {
        // 检查：是否已经登录
        if (!this.remoteService.isLogin()) {
            return;
        }

        for (String entityType : entityTypeList) {
            // 检查：是否已经进行reset过
            if (this.hasResetMap.contains(entityType)) {
                continue;
            }

            // 对云端进行reset操作
            if (!this.configEntityPublishService.operatePublishReset(entityType)) {
                continue;
            }

            // 保存：已经reset过
            this.hasResetMap.add(entityType);
        }
    }

    /**
     * 检测：本地和云端是否需要进行同步
     * 场景1：CLoud端主动发起的同步，Edge检测Cloud是否有reset标记，这个标记是由云端管理员手动发起的
     * 场景2：Edge端主动发起的同步，Edge检测Cloud和自己的时间戳是否保持一致，不一致为异常，此时强制发起reset
     */
    private void operatePublishCheck(Set<String> entityTypeList) {
        try {
            // 场景1：从云端查询，云端是否需要主动发起一次reset操作
            Set<String> resetList = this.configEntityPublishService.queryResetFlag(entityTypeList);
            this.hasResetMap.removeAll(resetList);

            // 场景2：比对必须本地和云端是否时间戳是否发生了失步的异常，正常是一致保持一致的，异常才会不一致，此时有本地发起reset
            Map<String, Object> timestampMap = this.configEntityPublishService.queryTimestamp(entityTypeList);
            for (String key : timestampMap.keySet()) {
                Map<String, Object> map = (Map<String, Object>) timestampMap.get(key);
                String timestamp = (String) map.get("timeStamp");

                // 检查云端返回的时间戳，跟本地redis的时间戳是否保持一致
                if (this.configEntityPublishService.operatePublishCheck(key, timestamp)) {
                    continue;
                }

                // 如果不一致，那么清除已经一致的标记
                this.hasResetMap.remove(key);
            }
        } catch (Exception e) {
            this.consoleService.error(e.getMessage());
            logger.error(e.getMessage());
        }
    }

    /**
     * 增量更新
     *
     * @param entityTypeList
     */
    private void operatePublishUpdate(Set<String> entityTypeList) throws IOException {
        // 检查：是否已经登录
        if (!this.remoteService.isLogin()) {
            return;
        }

        // 从云端查询时间戳
        Map<String, Object> timestampMap = this.configEntityPublishService.queryTimestamp(entityTypeList);

        for (String entityType : entityTypeList) {
            // 检查：是否已经进行reset过
            if (!this.hasResetMap.contains(entityType)) {
                continue;
            }

            Map<String, Object> map = (Map<String, Object>) timestampMap.get(entityType);
            String timestamp = (String) map.get("timeStamp");

            this.operatePublishUpdate(entityType, timestamp);
        }

    }

    private void operatePublishUpdate(String entityType, String timestamp) {
        try {
            // 检查：是否已经进行reset过
            if (!this.hasResetMap.contains(entityType)) {
                return;
            }

            AgileMapRedisService redisService = this.publishEntityManageService.getAgileMapService(entityType);

            // 装载数据：从redis读取数据，并获知变化状态
            Map<String, Object> addMap = new HashMap<>();
            Set<String> delSet = new HashSet<>();
            Map<String, Object> mdyMap = new HashMap<>();
            redisService.loadChangeEntities(addMap, delSet, mdyMap);

            // 检测：数据
            if (addMap.isEmpty() && delSet.isEmpty() && mdyMap.isEmpty()) {
                return;
            }

            // 打包数据
            Map<String, Object> data = new HashMap<>();
            data.put("update", mdyMap.values());
            data.put("insert", addMap.values());
            data.put("delete", delSet);

            // 检查：是否成功
            if (this.configEntityPublishService.operatePublishUpdate(entityType, timestamp, data)) {
                return;
            }

            // 更新失败，将已经reset，变为未reset，后面会重新校对
            this.hasResetMap.remove(entityType);
        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
        }
    }
}
