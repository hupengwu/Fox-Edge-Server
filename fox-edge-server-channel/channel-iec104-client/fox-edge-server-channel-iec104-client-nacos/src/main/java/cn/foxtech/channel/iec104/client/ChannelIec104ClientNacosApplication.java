package cn.foxtech.channel.iec104.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelIec104ClientNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelIec104ClientNacosApplication.class, args);
    }

}
