package cn.foxtech.device.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DeviceGraalvmApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceGraalvmApplication.class, args);
    }
}
