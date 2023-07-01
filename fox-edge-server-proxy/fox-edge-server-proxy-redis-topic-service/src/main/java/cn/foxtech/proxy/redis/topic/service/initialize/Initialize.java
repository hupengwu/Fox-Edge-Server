package cn.foxtech.proxy.redis.topic.service.initialize;


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


    /**
     * 进程状态
     */
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
