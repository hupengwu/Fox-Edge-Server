package cn.foxtech.channel.udp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelUdpClientNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelUdpClientNacosApplication.class, args);
    }

}
