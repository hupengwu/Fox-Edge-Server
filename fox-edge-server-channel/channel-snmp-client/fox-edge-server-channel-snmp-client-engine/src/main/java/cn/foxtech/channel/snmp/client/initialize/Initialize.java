package cn.foxtech.channel.snmp.client.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.snmp.client.service.ChannelService;
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
    private ChannelService channelService;

    @Autowired
    private ChannelInitialize channelInitialize;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.channelInitialize.initialize();

        this.channelService.initService();

        logger.info("------------------------初始化完成！------------------------");
    }
}
