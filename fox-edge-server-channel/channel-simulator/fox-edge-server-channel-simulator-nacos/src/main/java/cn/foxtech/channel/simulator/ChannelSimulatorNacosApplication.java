package cn.foxtech.channel.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelSimulatorNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelSimulatorNacosApplication.class, args);
    }

}
