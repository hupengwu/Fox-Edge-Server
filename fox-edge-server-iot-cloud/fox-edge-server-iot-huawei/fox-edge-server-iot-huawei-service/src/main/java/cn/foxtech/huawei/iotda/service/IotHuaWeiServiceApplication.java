package cn.foxtech.huawei.iotda.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class IotHuaWeiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotHuaWeiServiceApplication.class, args);
    }

}
