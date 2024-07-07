package cn.foxtech.channel.mqtt.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelMqttClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelMqttClientServiceApplication.class, args);
    }

}
