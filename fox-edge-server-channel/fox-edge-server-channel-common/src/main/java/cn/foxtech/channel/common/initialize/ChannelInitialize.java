package cn.foxtech.channel.common.initialize;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.scheduler.ChannelRedisScheduler;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.common.service.RedisTopicReportToDeviceService;
import cn.foxtech.channel.common.service.RedisTopicRespondDeviceService;
import cn.foxtech.channel.common.service.RedisTopicRespondManagerService;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.status.ServiceStatusScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 初始化
 */
@Component
public class ChannelInitialize {

    /**
     * redis的通道状态更新
     */
    @Autowired
    private ChannelRedisScheduler channelRedisScheduler;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    /**
     * Topic发布线程
     */
    @Autowired
    private RedisTopicRespondManagerService redisTopicRespondManagerService;

    /**
     * Topic发布线程
     */
    @Autowired
    private RedisTopicRespondDeviceService redisTopicRespondDeviceService;

    @Autowired
    private RedisTopicReportToDeviceService redisTopicReportToDeviceService;

    /**
     * 实体状态管理线程
     */
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ChannelProperties channelProperties;

    public void initialize() {
        Set<String> others = new HashSet<>();
        initialize(others);
    }

    public void initialize(Set<String> others) {
        // 启动进程状态通知
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 读取应用程序的配置
        this.channelProperties.initialize();

        // 装载实体
        Set<String> consumer = new HashSet<>();
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(ChannelEntity.class.getSimpleName());
        consumer.addAll(others);

        this.entityManageService.instance(consumer);
        this.entityManageService.initLoadEntity();

        // 启动独立的Topic发布线程：该线程不能阻塞
        this.redisTopicRespondDeviceService.schedule();
        this.redisTopicReportToDeviceService.schedule();
        this.redisTopicRespondManagerService.schedule();

        // 启动实体同步线程：该线程允许阻塞
        this.channelRedisScheduler.schedule();
    }
}
