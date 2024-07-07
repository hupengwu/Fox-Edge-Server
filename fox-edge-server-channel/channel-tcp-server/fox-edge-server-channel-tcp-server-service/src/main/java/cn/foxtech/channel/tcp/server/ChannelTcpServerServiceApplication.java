package cn.foxtech.channel.tcp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelTcpServerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelTcpServerServiceApplication.class, args);
    }

}
