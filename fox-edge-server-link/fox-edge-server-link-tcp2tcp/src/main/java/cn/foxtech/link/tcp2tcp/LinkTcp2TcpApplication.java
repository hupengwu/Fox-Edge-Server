package cn.foxtech.link.tcp2tcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LinkTcp2TcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkTcp2TcpApplication.class, args);
    }

}
