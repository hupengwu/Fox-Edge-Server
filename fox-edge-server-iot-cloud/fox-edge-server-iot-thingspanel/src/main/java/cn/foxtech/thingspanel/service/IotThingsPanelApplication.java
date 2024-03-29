package cn.foxtech.thingspanel.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class IotThingsPanelApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotThingsPanelApplication.class, args);
    }

}
