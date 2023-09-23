package cn.foxtech.channel.tcpsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelTcpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelTcpClientApplication.class, args);
    }

}
