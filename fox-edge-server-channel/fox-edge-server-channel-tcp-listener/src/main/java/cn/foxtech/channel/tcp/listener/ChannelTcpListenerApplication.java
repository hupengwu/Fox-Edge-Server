package cn.foxtech.channel.tcp.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelTcpListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelTcpListenerApplication.class, args);
    }

}
