package cn.foxtech.proxy.cloud.service.initialize;


import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.proxy.cloud.forwarder.initialize.InitializeForwarder;
import cn.foxtech.proxy.cloud.publisher.initialize.InitializePublisher;
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
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private InitializePublisher initializePublisher;

    @Autowired
    private InitializeForwarder initializeForwarder;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        this.initializePublisher.initialize();

        this.initializeForwarder.initialize();

        logger.info("------------------------初始化结束！------------------------");
    }
}
