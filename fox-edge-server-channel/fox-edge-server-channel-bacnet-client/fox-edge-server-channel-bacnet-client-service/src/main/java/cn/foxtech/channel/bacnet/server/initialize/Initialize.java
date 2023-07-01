package cn.foxtech.channel.bacnet.server.initialize;

import cn.foxtech.channel.bacnet.server.service.BACnetServerService;
import cn.foxtech.channel.common.initialize.ChannelInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    @Autowired
    private BACnetServerService serverService;

    @Autowired
    private ChannelInitialize channelInitialize;

    @Override
    public void run(String... args) {
        // 装载配置
        this.serverService.loadConfig();

        // 创建本地虚拟设备
        this.serverService.createLocalDevice();

        this.channelInitialize.initialize();
    }
}
