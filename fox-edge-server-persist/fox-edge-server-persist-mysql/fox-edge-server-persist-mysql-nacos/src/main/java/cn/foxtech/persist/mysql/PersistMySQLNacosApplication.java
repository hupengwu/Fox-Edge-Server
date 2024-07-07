package cn.foxtech.persist.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersistMySQLNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistMySQLNacosApplication.class, args);
    }

}
