package cn.foxtech.channel.http.client.initialize;


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
    private ChannelInitialize channelInitialize;

    @Override
    public void run(String... args) {
        this.channelInitialize.initialize();
    }
}
