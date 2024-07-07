package cn.foxtech.persist.iotdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersistIotDBNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistIotDBNacosApplication.class, args);
    }

}
