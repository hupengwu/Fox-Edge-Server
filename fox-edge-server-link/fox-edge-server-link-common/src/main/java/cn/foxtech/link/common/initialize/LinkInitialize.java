package cn.foxtech.link.common.initialize;

import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.common.scheduler.LinkRedisScheduler;
import cn.foxtech.link.common.service.EntityManageService;
import cn.foxtech.link.common.service.RedisTopicRespondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class LinkInitialize {
    /**
     * redis的通道状态更新
     */
    @Autowired
    private LinkRedisScheduler linkRedisScheduler;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    /**
     * Topic发布线程
     */
    @Autowired
    private RedisTopicRespondService redisTopicRespondService;

    /**
     * 实体状态管理线程
     */
    @Autowired
    private EntityManageService entityManageService;


    @Autowired
    private LinkProperties linkProperties;


    public void initialize() {
        // 启动进程状态通知
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 读取应用程序的配置
        this.linkProperties.initialize();

        // 装载实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动独立的Topic发布线程：该线程不能阻塞
        this.redisTopicRespondService.schedule();

        // 启动实体同步线程：该线程允许阻塞
        this.linkRedisScheduler.schedule();
    }
}
