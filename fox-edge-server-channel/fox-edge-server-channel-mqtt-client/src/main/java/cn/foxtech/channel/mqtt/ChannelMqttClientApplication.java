package cn.foxtech.channel.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelMqttClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelMqttClientApplication.class, args);
    }

}
