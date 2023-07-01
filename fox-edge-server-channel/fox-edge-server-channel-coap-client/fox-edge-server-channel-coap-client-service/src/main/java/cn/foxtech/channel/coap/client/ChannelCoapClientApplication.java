package cn.foxtech.channel.coap.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelCoapClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelCoapClientApplication.class, args);
    }

}
