package cn.foxtech.channel.gdana.digester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelGdanaDigesterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelGdanaDigesterApplication.class, args);
    }

}
