package cn.foxtech.channel.udp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelUdpServerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelUdpServerServiceApplication.class, args);
    }

}
