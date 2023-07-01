package cn.foxtech.channel.proxy.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ChannelProxyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelProxyServerApplication.class, args);
    }

}
