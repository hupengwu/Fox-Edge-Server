package cn.foxtech.persist.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersistMySQLServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistMySQLServiceApplication.class, args);
    }

}
