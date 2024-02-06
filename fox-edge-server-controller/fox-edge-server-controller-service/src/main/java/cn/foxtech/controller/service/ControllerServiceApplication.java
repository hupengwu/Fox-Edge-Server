package cn.foxtech.controller.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ControllerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllerServiceApplication.class, args);
    }

}
