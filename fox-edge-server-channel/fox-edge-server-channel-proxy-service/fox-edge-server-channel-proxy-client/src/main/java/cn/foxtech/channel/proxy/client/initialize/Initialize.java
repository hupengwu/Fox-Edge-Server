package cn.foxtech.channel.proxy.client.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.proxy.client.service.MqttClientService;
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
    private MqttClientService mqttClientService;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 通道服务的基础初始化：此时会从redis中装载自己需要的配置信息
        this.channelInitialize.initialize();

        // 初始化mqtt链接
        this.mqttClientService.Initialize();


        logger.info("------------------------初始化结束！------------------------");
    }
}
