package cn.foxtech.iot.fox.cloud.service.initialize;


import cn.foxtech.iot.fox.cloud.common.initialize.InitializeCommon;
import cn.foxtech.iot.fox.cloud.forwarder.initialize.InitializeForwarder;
import cn.foxtech.iot.fox.cloud.publisher.initialize.InitializePublisher;
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
    private InitializeCommon initializeCommon;

    @Autowired
    private InitializePublisher initializePublisher;

    @Autowired
    private InitializeForwarder initializeForwarder;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.initializeCommon.initialize();

        this.initializePublisher.initialize();

        this.initializeForwarder.initialize();

        logger.info("------------------------初始化结束！------------------------");
    }
}
