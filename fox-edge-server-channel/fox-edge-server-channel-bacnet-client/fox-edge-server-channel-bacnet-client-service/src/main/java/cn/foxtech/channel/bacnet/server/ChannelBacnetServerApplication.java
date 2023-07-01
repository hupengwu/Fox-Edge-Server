package cn.foxtech.channel.bacnet.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelBacnetServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelBacnetServerApplication.class, args);
    }

}
