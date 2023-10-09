package cn.foxtech.channel.s7plc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelS7PLCApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelS7PLCApplication.class, args);
    }

}
