package cn.foxtech.channel.common.initialize;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.linker.LinkerMethodScanner;
import cn.foxtech.channel.common.linker.LinkerScheduler;
import cn.foxtech.channel.common.scheduler.ChannelRedisScheduler;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.common.service.RedisTopicRespondDeviceService;
import cn.foxtech.channel.common.service.RedisTopicRespondManagerService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 实体状态管理线程
     */
    @Autowired
    private EntityManageService entityManageService;


    /**
     * 简单链路
     */
    @Autowired
    private LinkerScheduler linkerScheduler;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private LinkerMethodScanner linkerMethodScanner;


    public void initialize() {
        // 启动进程状态通知
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 读取应用程序的配置
        this.channelProperties.initialize();

        // 装载实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动独立的Topic发布线程：该线程不能阻塞
        this.redisTopicRespondDeviceService.schedule();
        this.redisTopicRespondManagerService.schedule();

        // 启动实体同步线程：该线程允许阻塞
        this.channelRedisScheduler.schedule();


        // 开启链路模式
        if (this.channelProperties.getLinkerMode()) {
            this.linkerMethodScanner.loadJar();
            this.linkerMethodScanner.scanMethod();

            this.linkerScheduler.initialize();
            this.linkerScheduler.schedule();
        }
    }
}
