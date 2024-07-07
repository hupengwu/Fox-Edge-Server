package cn.foxtech.channel.udp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelUdpServerNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelUdpServerNacosApplication.class, args);
    }

}
