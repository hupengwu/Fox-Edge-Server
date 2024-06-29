package cn.foxtech.channel.common.scheduler;


import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.ChannelStatusUpdater;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 进程状态调度器：把进程状态周期性的刷新到redis
 */
@Component
public class EntityManageScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(EntityManageScheduler.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 通道服务
     */
    @Autowired
    private ChannelClientAPI channelService;

    /**
     * 配置信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private ChannelStatusUpdater channelStatusUpdater;

    /**
     * 通道配置
     */
    private Map<String, ChannelEntity> channelEntityMap;

    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 同步实体数据
        this.entityManageService.syncEntity();

        // 重置通道的配置信息
        this.syncChannelConfig();
    }

    /**
     * 同步通道配置
     */
    private void syncChannelConfig() {
        // 检查：是否有重新状态的配置到达
        String channelType = this.channelProperties.getChannelType();
        Long updateTime = this.entityManageService.removeReloadedFlag(ChannelEntity.class.getSimpleName());
        if (updateTime == null && this.channelEntityMap != null) {
            return;
        }

        // 取出重新状态的配置
        List<BaseEntity> entityList = this.entityManageService.getChannelEntity(channelType);
        Map<String, ChannelEntity> map = new HashMap<>();
        for (BaseEntity entity : entityList) {
            ChannelEntity channelEntity = (ChannelEntity) entity;
            map.put(channelEntity.getChannelName(), channelEntity);
        }

        // 检查：是否为初始化状态
        if (this.channelEntityMap == null) {
            this.channelEntityMap = new HashMap<>();
        }

        // 比较差异
        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(this.channelEntityMap.keySet(), map.keySet(), addList, delList, eqlList);

        // 打开通道
        for (String key : addList) {
            try {
                // 打开通道的南向
                ChannelEntity channelEntity = map.get(key);
                this.channelService.openChannel(channelEntity.getChannelName(), channelEntity.getChannelParam());
                this.channelEntityMap.put(key, channelEntity);

                // 更新通道的南向打开状态
                this.channelStatusUpdater.updateOpenStatus(key, true);

                String message = "通道打开成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "通道打开失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);

                this.channelStatusUpdater.updateOpenStatus(key, false);
            }
        }

        // 关闭通道
        for (String key : delList) {
            try {
                ChannelEntity channelEntity = this.channelEntityMap.get(key);

                this.channelService.closeChannel(channelEntity.getChannelName(), channelEntity.getChannelParam());
                this.channelEntityMap.remove(key);

                // 更新通道的南向打开状态
                this.channelStatusUpdater.updateOpenStatus(key, false);


                String message = "通道关闭成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "通道关闭失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);
            }
        }

        // 重新打开通道
        for (String key : eqlList) {
            try {
                ChannelEntity oldEntity = this.channelEntityMap.get(key);
                ChannelEntity newEntity = map.get(key);

                // 检测：配置内容是否发生了变化
                String newValue = newEntity.makeServiceValue();
                String oldValue = oldEntity.makeServiceValue();
                if (newValue.equals(oldValue)) {
                    continue;
                }

                // 关闭通道
                this.channelService.closeChannel(oldEntity.getChannelName(), oldEntity.getChannelParam());
                this.channelEntityMap.remove(key);

                // 更新通道的南向打开状态
                this.channelStatusUpdater.updateOpenStatus(key, false);

                // 重新打开通道
                this.channelService.openChannel(newEntity.getChannelName(), newEntity.getChannelParam());
                this.channelEntityMap.put(key, newEntity);

                // 更新通道的南向打开状态
                this.channelStatusUpdater.updateOpenStatus(key, true);

                String message = "通道重置成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "通道重置失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);
            }
        }
    }
}
