package cn.foxtech.value.ex.task.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DeviceValueExTaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceValueExTaskServiceApplication.class, args);
    }

}
