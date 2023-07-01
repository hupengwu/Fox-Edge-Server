package cn.foxtech.channel.udpsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelUdpSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelUdpSocketApplication.class, args);
    }

}
