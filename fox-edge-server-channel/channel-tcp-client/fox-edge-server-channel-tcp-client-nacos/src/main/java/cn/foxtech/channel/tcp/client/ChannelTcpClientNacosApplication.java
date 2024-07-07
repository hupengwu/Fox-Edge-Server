package cn.foxtech.channel.tcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelTcpClientNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelTcpClientNacosApplication.class, args);
    }

}
