package cn.foxtech.channel.snmp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChannelSnmpClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelSnmpClientServiceApplication.class, args);
    }

}
