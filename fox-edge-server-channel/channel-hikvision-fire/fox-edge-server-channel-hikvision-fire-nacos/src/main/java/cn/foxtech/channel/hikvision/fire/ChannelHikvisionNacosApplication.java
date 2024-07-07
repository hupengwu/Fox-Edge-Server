package cn.foxtech.channel.hikvision.fire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelHikvisionNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelHikvisionNacosApplication.class, args);
    }

}
