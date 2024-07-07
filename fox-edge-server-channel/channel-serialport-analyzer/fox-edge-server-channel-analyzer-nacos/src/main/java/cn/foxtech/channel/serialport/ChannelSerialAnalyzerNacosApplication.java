package cn.foxtech.channel.serialport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelSerialAnalyzerNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelSerialAnalyzerNacosApplication.class, args);
    }

}
