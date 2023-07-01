package cn.foxtech.channel.coap.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelHttpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelHttpClientApplication.class, args);
    }

}
