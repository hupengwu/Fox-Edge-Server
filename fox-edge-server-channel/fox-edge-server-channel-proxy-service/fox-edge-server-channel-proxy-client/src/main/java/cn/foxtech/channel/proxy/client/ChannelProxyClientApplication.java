package cn.foxtech.channel.proxy.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelProxyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelProxyClientApplication.class, args);
    }

}
