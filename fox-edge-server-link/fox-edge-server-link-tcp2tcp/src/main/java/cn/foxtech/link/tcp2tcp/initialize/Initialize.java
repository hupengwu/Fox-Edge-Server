package cn.foxtech.link.tcp2tcp.initialize;

import cn.foxtech.link.tcp2tcp.service.ServerInitializer;
import cn.foxtech.link.common.initialize.LinkInitialize;
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
    private LinkInitialize linkInitialize;

    @Autowired
    private ServerInitializer serverInitializer;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.linkInitialize.initialize();

        logger.info("------------------------初始化结束！------------------------");

    }
}
