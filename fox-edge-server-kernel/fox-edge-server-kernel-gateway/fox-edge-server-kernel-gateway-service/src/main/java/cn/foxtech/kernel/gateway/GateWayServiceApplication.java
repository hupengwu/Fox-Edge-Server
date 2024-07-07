package cn.foxtech.kernel.gateway;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GateWayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayServiceApplication.class, args);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
    }

}
