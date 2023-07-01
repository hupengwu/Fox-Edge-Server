package cn.foxtech.channel.iec104.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelIec104MasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelIec104MasterApplication.class, args);
    }

}
