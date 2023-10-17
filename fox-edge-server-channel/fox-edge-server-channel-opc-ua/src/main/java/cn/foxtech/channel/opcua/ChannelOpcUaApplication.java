package cn.foxtech.channel.opcua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ChannelOpcUaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelOpcUaApplication.class, args);
    }

}
