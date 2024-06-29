package cn.foxtech.channel.common.initialize;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.scheduler.EntityManageScheduler;
import cn.foxtech.channel.common.scheduler.RedisListRespondScheduler;
import cn.foxtech.channel.common.scheduler.RedisListReportToScheduler;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.common.rpc.redis.channel.server.RedisListChannelServer;
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
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private RedisListReportToScheduler redisListReportToScheduler;

    @Autowired
    private RedisListRespondScheduler redisListRespondScheduler;

    /**
     * 实体状态管理线程
     */
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private RedisListChannelServer redisListChannelServer;


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

        // 绑定通道类型
        this.redisListChannelServer.setChannelType(this.channelProperties.getChannelType());

        // 装载实体
        this.entityManageService.instance(others);
        this.entityManageService.initLoadEntity();

        // 启动独立的Topic发布线程：该线程不能阻塞
        this.redisListReportToScheduler.schedule();
        this.redisListRespondScheduler.schedule();

        // 启动实体同步线程：该线程允许阻塞
        this.entityManageScheduler.schedule();
    }
}
