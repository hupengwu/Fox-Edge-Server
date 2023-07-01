package cn.foxtech.channel.proxy.client.initialize;


import cn.foxtech.channel.proxy.client.service.EntityManageService;
import cn.foxtech.channel.proxy.client.service.MqttClientService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    /**
     * 实体状态管理线程
     */
    @Autowired
    private EntityManageService entityManageService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 启动进程状态通知
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();


        // 装载实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        this.mqttClientService.Initialize();

        logger.info("------------------------初始化结束！------------------------");
    }
}
