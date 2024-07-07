package cn.foxtech.value.ex.task.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DeviceValueExTaskNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceValueExTaskNacosApplication.class, args);
    }

}
