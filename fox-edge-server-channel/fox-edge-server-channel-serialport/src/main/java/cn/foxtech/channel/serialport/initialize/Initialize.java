package cn.foxtech.channel.serialport.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.serialport.service.ChannelService;
import cn.foxtech.common.entity.manager.InitialConfigService;
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
    private ChannelInitialize channelInitialize;

    @Autowired
    private InitialConfigService configService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.channelInitialize.initialize();

        this.configService.initialize("serverConfig", "serverConfig.json");

        logger.info("------------------------初始化结束！------------------------");

    }
}
