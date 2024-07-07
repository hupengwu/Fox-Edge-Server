package cn.foxtech.channel.tcp.client.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.tcp.client.service.ServerInitializer;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private ChannelInitialize channelInitialize;

    @Autowired
    private ServerInitializer serverInitializer;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.channelInitialize.initialize();
        this.serverInitializer.initialize();

        logger.info("------------------------初始化结束！------------------------");

    }
}
