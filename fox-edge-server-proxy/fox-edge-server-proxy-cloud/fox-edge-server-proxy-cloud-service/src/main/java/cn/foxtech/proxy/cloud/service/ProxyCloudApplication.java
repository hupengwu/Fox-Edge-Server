package cn.foxtech.proxy.cloud.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class ProxyCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyCloudApplication.class, args);
    }

}
