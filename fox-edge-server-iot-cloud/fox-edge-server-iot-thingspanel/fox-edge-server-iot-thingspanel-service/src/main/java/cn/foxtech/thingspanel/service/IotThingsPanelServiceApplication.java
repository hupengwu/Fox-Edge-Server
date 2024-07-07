package cn.foxtech.thingspanel.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class IotThingsPanelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotThingsPanelServiceApplication.class, args);
    }

}
