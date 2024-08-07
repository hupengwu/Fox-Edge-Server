package cn.foxtech.thingsboard.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class IotThingsBoardNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotThingsBoardNacosApplication.class, args);
    }

}
