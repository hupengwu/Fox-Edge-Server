package cn.foxtech.channel.common.scheduler;


import cn.foxtech.channel.common.api.ChannelClientAPI;
import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.common.linker.LinkerManager;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 进程状态调度器：把进程状态周期性的刷新到redis
 */
@Data
@Component
public class ChannelRedisScheduler extends PeriodTaskService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

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

    /**
     * 通道配置
     */
    private Map<String, ChannelEntity> channelEntityMap;

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
                ChannelEntity channelEntity = map.get(key);
                this.channelService.openChannel(channelEntity.getChannelName(), channelEntity.getChannelParam());
                this.channelEntityMap.put(key, channelEntity);

                // 标识链路失效
                LinkerManager.registerChannel(channelEntity.getChannelName());
            } catch (Exception e) {
                logger.error(e);
            }
        }

        // 关闭通道
        for (String key : delList) {
            try {
                ChannelEntity channelEntity = this.channelEntityMap.get(key);

                // 标识链路失效
                LinkerManager.unregisterChannel(channelEntity.getChannelName());

                this.channelService.closeChannel(channelEntity.getChannelName(), channelEntity.getChannelParam());
                this.channelEntityMap.remove(key);
            } catch (Exception e) {
                logger.error(e);
            }
        }

        // 重新打开通道
        for (String key : eqlList) {
            try {
                ChannelEntity oldEntity = this.channelEntityMap.get(key);
                ChannelEntity newEntity = map.get(key);
                if (newEntity.makeServiceValue().equals(oldEntity.makeServiceKey())) {
                    continue;
                }

                this.channelService.closeChannel(oldEntity.getChannelName(), oldEntity.getChannelParam());
                this.channelService.openChannel(newEntity.getChannelName(), newEntity.getChannelParam());
                this.channelEntityMap.put(key, newEntity);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


}
