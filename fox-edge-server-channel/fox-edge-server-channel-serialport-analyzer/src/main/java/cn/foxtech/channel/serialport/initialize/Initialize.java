package cn.foxtech.channel.serialport.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.serialport.service.ServerInitializer;
import cn.foxtech.common.entity.entity.OperateEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    private ChannelInitialize channelInitialize;

    @Autowired
    private ServerInitializer serverInitializer;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 初始化装载数据
        Set<String> consumer = new HashSet<>();
        consumer.add(OperateEntity.class.getSimpleName());
        this.channelInitialize.initialize(consumer);

        this.serverInitializer.initialize();

        logger.info("------------------------初始化结束！------------------------");

    }
}
