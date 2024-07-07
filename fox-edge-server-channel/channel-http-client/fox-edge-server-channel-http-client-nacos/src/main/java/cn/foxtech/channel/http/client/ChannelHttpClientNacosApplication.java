package cn.foxtech.channel.http.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelHttpClientNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelHttpClientNacosApplication.class, args);
    }

}
